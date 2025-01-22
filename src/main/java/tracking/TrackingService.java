package tracking;

import javafx.scene.paint.PhongMaterial;
import shapes.NeedleProjection;
import shapes.TrackingCone;
import tracking.observers.TrackingMeasurementObserver;
import tracking.observers.TrackingObserver;
import tracking.observers.TrackingSourceObserver;
import tracking.tracker.TrackingSource;
import util.HardwareStatus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The TrackingService is a singleton that keeps track of the currently used tools along with their respective history.
 * It provides methods to take a new measurement or to do so at a certain frequency.
 * Both ways update the states of the tools and issue an event for all subscribers.
 * Using the observer mechanism, it is possible to easily subscribe for Measurement and connection changes.
 */

public final class TrackingService {
    // For singleton
    private static TrackingService trackingService;
    private TrackingService(){}
    public static TrackingService getInstance(){
        if(trackingService == null){
            trackingService = new TrackingService();
        }
        return trackingService;
    }

    private TrackingSource trackingSource;

    private final List<TrackingMeasurementObserver> measurementObservers = new ArrayList<>();
    private final List<TrackingSourceObserver> sourceObservers = new ArrayList<>();

    private final ScheduledExecutorService updateScheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> updateSchedulerFuture;

    private final Map<String, Tool> tools = new HashMap<>();

    // Cache for rendered tools. This might contain relicts of tools that have been deleted in the source, so do not work with this cache directly.
    private final Map<String, RenderedTool> renderedTools = new HashMap<>();

    public List<Tool> getTools(){
        return new ArrayList<>(tools.values());
    }

    public List<RenderedTool> getToolsForRender(){
        var returnList = new ArrayList<RenderedTool>();
        for(var toolname: tools.keySet()){
            if(renderedTools.containsKey(toolname)){
                returnList.add(renderedTools.get(toolname));
            }else{
                var renderedTool = new RenderedTool(tools.get(toolname));
                renderedTools.put(toolname, renderedTool);
                returnList.add(renderedTool);
            }
        }
        return returnList;
    }

    public Map<String, Measurement> updateSingle(){
        // This will fetch one measurement, convert it into a Tool (or better: update the current list of tools with this measurement to be the current one, but also move it into their history)
        // Then it will notify all observers with this updated ToolList
        if(!trackingSource.isConnected()){
            throw new IllegalStateException("TrackingSource is not connected");
        }
        var measurement = trackingSource.measure();
        for(var toolname: measurement.keySet()){
            if(!tools.containsKey(toolname)){
                tools.put(toolname, new Tool(toolname));
            }
            tools.get(toolname).addMeasurement(measurement.get(toolname));
        }

        // Attention: When observers alter data in the map, it is altered for everyone!
        for(var observer: measurementObservers){
            observer.onMeasurement(measurement);
        }

        return measurement;
    }

    public void scheduleUpdates(int ms){
        // Creates a timeline or something which periodically calls updateSingle()
        updateSchedulerFuture = updateScheduler.scheduleAtFixedRate(this::updateSingle, 0, ms, TimeUnit.MILLISECONDS);
        sourceObservers.forEach(o -> o.onTrackingSourceStatusChanged(HardwareStatus.CONNECTED_AND_STREAMING));
    }

    public void cancelUpdates(){
        updateSchedulerFuture.cancel(true);
        sourceObservers.forEach(o -> o.onTrackingSourceStatusChanged(HardwareStatus.CONNECTED_NO_STREAM));
    }

    // TODO: Perhaps these observers become invalid once destroyed, Test!
    public <T extends TrackingObserver> T subscribe(T observer) {
        if (observer instanceof TrackingMeasurementObserver o) {
            measurementObservers.add(o);
        }else if(observer instanceof TrackingSourceObserver o) {
            sourceObservers.add(o);
        }
        return observer;
    }

    public <T extends TrackingObserver> void unsubscribe(T observer){
        if (observer instanceof TrackingMeasurementObserver o) {
            measurementObservers.remove(o);
        }else if(observer instanceof TrackingSourceObserver o) {
            sourceObservers.remove(o);
        }
    }


    public void connect(TrackingSource source) throws IOException {
        if (trackingSource != null){
            disconnect();
        }
        trackingSource = source;
        trackingSource.connect();
        sourceObservers.forEach(o -> o.onTrackingSourceStatusChanged(HardwareStatus.CONNECTED_NO_STREAM));
    }

    public void disconnect(){
        cancelUpdates();
        trackingSource.disconnect();
        trackingSource = null;
        // This will clear all tools to avoid mixtures when switching sources
        tools.clear();
        sourceObservers.forEach(o -> o.onTrackingSourceStatusChanged(HardwareStatus.DISCONNECTED));
    }

    public HardwareStatus getStatus(){
        if(!trackingSource.isConnected()){
            return HardwareStatus.DISCONNECTED;
        }
        if(updateSchedulerFuture == null || updateSchedulerFuture.isCancelled()){
            return HardwareStatus.CONNECTED_NO_STREAM;
        }else{
            return HardwareStatus.CONNECTED_AND_STREAMING;
        }
    }


    public boolean isConnected(){
        return (trackingSource != null && trackingSource.isConnected());
    }
}
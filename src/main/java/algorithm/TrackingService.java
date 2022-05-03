package algorithm;

import inputOutput.AbstractTrackingDataSource;
import javafx.animation.Timeline;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to manage the access to the OpenIGTLink-Connection and therefore to the trackers.
 * It is a singleton in order to provide the same reference to all users.
 * It should be used to get the reference to the currently active TrackingService, DataService and Timeline.
 */
public final class TrackingService {
    private static TrackingService trackingService;
    private AbstractTrackingDataSource trackingDataSource;
    private DataService dataService;
    private Timeline timeline;
    private final List<TrackingServiceObserver> observers = new ArrayList<>();

    private TrackingService(){}

    /**
     * Returns the instance. This class is a singleton, which means that there only is one and the same instance at a time.
     * @return The instance
     */
    public static TrackingService getInstance(){
        if(trackingService == null){
            trackingService = new TrackingService();
        }
        return trackingService;
    }

    /**
     * Register an observer. Observers are triggered when the references to any attribute are changed.
     * @param observer The observer that shall be triggered
     */
    public void registerObserver(TrackingServiceObserver observer){
        observers.add(observer);
    }

    /**
     * Sets the reference to the tracking source.
     * @param trackingDataSource The new tracking source
     */
    public void changeTrackingSource(AbstractTrackingDataSource trackingDataSource){
        this.trackingDataSource = trackingDataSource;

        // Trigger all observers
        observers.forEach(observer -> observer.onTrackingSourceChanged(true, false, false));
    }

    /**
     * Sets the reference to the data service.
     * @param dataService the new dataservice
     */
    public void changeDataService(DataService dataService){
        this.dataService = dataService;

        // Trigger all observers
        observers.forEach(observer -> observer.onTrackingSourceChanged(false, true, false));
    }

    /**
     * Sets the reference to the timeline object.
     * @param timeline The new timeline
     */
    public void changeTimeline(Timeline timeline){
        this.timeline = timeline;

        // Trigger all observers
        observers.forEach(observer -> observer.onTrackingSourceChanged(false, false, true));
    }

    /**
     * Returns the reference to the tracking source
     * @return The currently active tracking source
     */
    public AbstractTrackingDataSource getTrackingDataSource(){
        return trackingDataSource;
    }

    /**
     * Returns the reference to the data service
     * @return The currently active data service
     */
    public DataService getDataService(){
        return dataService;
    }

    /**
     * Returns the reference to the timeline
     * @return The currently active timeline
     */
    public Timeline getTimeline(){
        return timeline;
    }
}

package algorithm;

import inputOutput.AbstractTrackingDataSource;
import javafx.animation.Timeline;

import java.util.ArrayList;
import java.util.List;

public final class TrackingService {
    private static TrackingService trackingService;
    private AbstractTrackingDataSource trackingDataSource;
    private DataService dataService;
    private Timeline timeline;
    private final List<TrackingServiceObserver> observers = new ArrayList<>();

    private TrackingService(){}

    public static TrackingService getInstance(){
        if(trackingService == null){
            trackingService = new TrackingService();
        }
        return trackingService;
    }

    public void registerObserver(TrackingServiceObserver observer){
        observers.add(observer);
    }

    public void changeTrackingSource(AbstractTrackingDataSource trackingDataSource){
        this.trackingDataSource = trackingDataSource;

        // Trigger all observers
        observers.forEach(observer -> observer.onTrackingSourceChanged(true, false, false));
    }
    public void changeDataService(DataService dataService){
        this.dataService = dataService;

        // Trigger all observers
        observers.forEach(observer -> observer.onTrackingSourceChanged(false, true, false));
    }
    public void changeTimeline(Timeline timeline){
        this.timeline = timeline;

        // Trigger all observers
        observers.forEach(observer -> observer.onTrackingSourceChanged(false, false, true));
    }

    public AbstractTrackingDataSource getTrackingDataSource(){
        return trackingDataSource;
    }
    public DataService getDataService(){
        return dataService;
    }
    public Timeline getTimeline(){
        return timeline;
    }
}

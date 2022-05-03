package algorithm;

/**
 * Interface for defining a callback-method for being triggered upon a change in references in TrackingService.
 */
public interface TrackingServiceObserver {
    /**
     * This method is triggered once a reference to one of the attributes of the TrackingService changed
     * @param sourceChanged true, if the TrackingSource changed
     * @param dataServiceChanged true, if the DataService changed
     * @param timelineChanged true, if the timeline changed.
     */
    void onTrackingSourceChanged(boolean sourceChanged, boolean dataServiceChanged, boolean timelineChanged);
}

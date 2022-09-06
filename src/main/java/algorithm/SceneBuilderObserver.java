package algorithm;

/**
 * Interface for defining a callback-method for being triggered upon a change in references in SceneBuilder.
 */
public interface SceneBuilderObserver {
    /**
     * This method is triggered once a reference to one of the attributes of the TrackingService changed
     * @param paneChanged true, if the TrackingSource changed
     * @param meshGroupChanged true, if the DataService changed
     */
    void onViewChanged(boolean paneChanged, boolean meshGroupChanged);
    //TODO change parameters
}

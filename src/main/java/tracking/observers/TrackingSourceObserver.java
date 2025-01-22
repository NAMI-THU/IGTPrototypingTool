package tracking.observers;

import util.HardwareStatus;

public interface TrackingSourceObserver extends TrackingObserver {
    /**
     * This method is triggered once the status of the tracking source changed, e.g. a source connected or disconnected
     */
    void onTrackingSourceStatusChanged(HardwareStatus newState);
}
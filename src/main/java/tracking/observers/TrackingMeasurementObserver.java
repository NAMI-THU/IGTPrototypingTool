package tracking.observers;

import tracking.Measurement;

import java.util.Map;

public interface TrackingMeasurementObserver extends TrackingObserver {
    /**
     * This method is triggered every time a new datapoint is received from the tracking source
     */
    void onMeasurement(Map<String, Measurement> toolMeasurements);
}

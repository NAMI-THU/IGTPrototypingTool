package tracking.tracker;

import tracking.Measurement;

import java.io.IOException;
import java.util.Map;

public interface TrackingSource {
    Map<String, Measurement> measure();

    void connect() throws IOException;
    void disconnect();

    boolean isConnected();
}

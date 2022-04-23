package algorithm;

import algorithm.DataService;
import inputOutput.AbstractTrackingDataSource;
import javafx.animation.Timeline;

public interface TrackingServiceObserver {
    void onTrackingSourceChanged(boolean sourceChanged, boolean dataServiceChanged, boolean timelineChanged);
}

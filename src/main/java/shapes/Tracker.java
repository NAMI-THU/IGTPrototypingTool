package shapes;

import util.Quaternion;
import util.Vector3D;

import java.util.LinkedList;
import java.util.List;

public class Tracker {
    private String trackerName;
    private String customName;
    private List<TrackerData> trackerDataList = new LinkedList<>();

    public Tracker(double timestamp, double valid, double coordinate_x, double coordinate_y, double coordinate_z,
                       double rotation_x, double rotation_y, double rotation_z, double rotation_r, String name) {
        this.addTrackerData(new TrackerData(timestamp, valid, coordinate_x, coordinate_y,
                coordinate_z, rotation_x, rotation_y, rotation_z, rotation_r,
                name));
        this.trackerName = name;
        this.customName = name;
    }

    public List<TrackerData> getTrackerDataList() {
        return trackerDataList;
    }

    public void addTrackerData(TrackerData td) {
        trackerDataList.add(td);
    }
    public String getTrackerName() {
        return trackerName;
    }
}

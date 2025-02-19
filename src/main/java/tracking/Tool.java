package tracking;

import util.Quaternion;
import util.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class Tool {
    private String name;
    private List<Measurement> history;

    public Tool(String name){
        this.name = name;
        this.history = new ArrayList<>();
    }

    public Measurement getLatestMeasurement(){
        return history.get(history.size()-1);
    }

    public List<Measurement> getLatestNMeasurements(int n){
        return history.subList(history.size() - n, history.size());
    }

    public void addMeasurement(Measurement measurement) {
        history.add(measurement);
    }

    public String getName() {
        return name;
    }

    public List<Measurement> getMeasurementHistory() {
        return history;
    }

    // Name, Color, Transform, history
}

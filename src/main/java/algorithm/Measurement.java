package algorithm;

import com.jme3.math.Quaternion;
import inputOutput.Tool;
import javafx.geometry.Point3D;

public class Measurement {

    private Point3D point;
    private double error;
    private double timestamp;
    private String toolname;
    private Quaternion rotation;

    public Measurement(Tool tool) {
        this.setPoint(new Point3D(tool.getCoordinate().getX(),
                tool.getCoordinate().getY(),
                tool.getCoordinate().getZ()));
        this.setRotation(new Quaternion().set((float) tool.getRotationX(),
                (float) tool.getRotationY(),
                (float) tool.getRotationZ(),
                (float) tool.getRotationR()));
        this.setTimestamp(tool.getTimestamp());
        this.setToolname(tool.getName());

    }

    public Measurement() {

    }

    public Quaternion getRotation() {
        return rotation;
    }

    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    public Point3D getPoint() {
        return point;
    }

    public void setPoint(Point3D point) {
        this.point = point;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public String getToolname() {
        return toolname;
    }

    public void setToolname(String toolname) {
        this.toolname = toolname;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }
}

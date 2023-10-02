package algorithm;

import com.google.gson.annotations.Expose;
import inputOutput.Tool;
import javafx.geometry.Point3D;
import util.Quaternion;

public class Measurement {

    private Point3D point;
    @Expose
    private double error;
    @Expose
    private double timestamp;
    @Expose
    private String toolname;
    private Quaternion rotation;
    @Expose
    private double x,y,z;
    @Expose
    private double rot_x, rot_y, rot_z, rot_r;

    public Measurement(Tool tool) {
        x = tool.getCoordinate().getX();
        y = tool.getCoordinate().getY();
        z = tool.getCoordinate().getZ();

        rot_x = (float) tool.getRotationX();
        rot_y = (float) tool.getRotationY();
        rot_z = (float) tool.getRotationZ();
        rot_r = (float) tool.getRotationR();

        this.setPoint(new Point3D(x,y,z));
        this.setRotation(new Quaternion().set(rot_r, rot_x, rot_y, rot_z));
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
        rot_x = rotation.getX();
        rot_y = rotation.getY();
        rot_z = rotation.getZ();
        rot_r = rotation.getW();
    }

    public Point3D getPoint() {
        return point;
    }

    public void setPoint(Point3D point) {
        this.point = point;
        x = point.getX();
        y = point.getY();
        z = point.getZ();
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

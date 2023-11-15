package algorithm;

import com.google.gson.annotations.Expose;
import inputOutput.TempTool;
import util.Quaternion;
import util.Vector3D;

public class Measurement {
    private Vector3D pos;
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

    public Measurement(TempTool tempTool) {
        x = tempTool.getCoordinate().getX();
        y = tempTool.getCoordinate().getY();
        z = tempTool.getCoordinate().getZ();

        rot_x = (float) tempTool.getRotationX();
        rot_y = (float) tempTool.getRotationY();
        rot_z = (float) tempTool.getRotationZ();
        rot_r = (float) tempTool.getRotationR();

        this.setPos(new Vector3D(x,y,z));
        this.setRotation(new Quaternion().set(rot_r, rot_x, rot_y, rot_z));
        this.setTimestamp(tempTool.getTimestamp());
        this.setToolname(tempTool.getName());


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

    public Vector3D getPos() {
        return pos;
    }

    public void setPos(Vector3D pos) {
        this.pos = pos;
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

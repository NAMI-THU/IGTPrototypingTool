package shapes;

import util.Quaternion;
import util.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class TrackerData {
    private Vector3D pos;
    private Quaternion rotation;
    private String initialName;
    private double timestamp;
    private double valid;

    /* Average Measurements */
    private Quaternion rotationJitter;
    private Quaternion rotationError;
    private List<Double> errors = new ArrayList<>();




    public TrackerData(double timestamp, double valid, double coordinate_x, double coordinate_y, double coordinate_z,
                       double rotation_x, double rotation_y, double rotation_z, double rotation_r, String name) {
        this.timestamp = timestamp;
        this.valid = valid;
        this.initialName = name;
        pos = new Vector3D(coordinate_x, coordinate_y, coordinate_z);
        rotation = new Quaternion(rotation_r, rotation_x, rotation_y, rotation_z);
    }

    public TrackerData(double timestamp, double valid, Vector3D pos, Quaternion rotation, String name) {
        this.timestamp = timestamp;
        this.valid = valid;
        this.pos = pos;
        this.rotation = rotation;
        this.initialName = name;
    }

    public void setData(double timestamp, double valid, Vector3D pos, Quaternion rotation, String name) {
        this.timestamp = timestamp;
        this.valid = valid;
        this.pos = pos;
        this.rotation = rotation;
        this.initialName = name;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public Vector3D getPos() {
        return pos;
    }

    public String getName() {
        return initialName;
    }

    /**
     * This method computes the root mean square error. She receives a list of
     * errors. In a loop the errors are added and taken in square. The RMSE is
     * calculated with the square of additionalPowError divided by the number of
     * errors.
     *
     * @return rmse - the Root Mean Square Error, which is just the square root
     * of the mean square error
     */
    private double getRMSE() {
        double additionalPowError = 0;

        for (double error : errors) {
            additionalPowError += Math.pow(error, 2);
        }
        double rmse = Math.sqrt(additionalPowError / errors.size());
        return rmse;
    }

    /**
     * The method getJitter computes the root mean square error. She receives a
     * list of errors and called method getRMSE, where the RMSE is calculated.
     *
     * @return getRMSE - the root mean square error
     */
    public double getJitter() {
        return getRMSE();
    }
}

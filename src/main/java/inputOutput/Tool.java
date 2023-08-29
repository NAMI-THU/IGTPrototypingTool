package inputOutput;

import javafx.geometry.Point3D;
import com.jme3.math.Quaternion;

/**
 * This class is used as an object to temporary save the values of a tool,
 * which are separated in CSVFileReader. These tool objects will be cached in
 * an ArrayList which is used later for various calculation.
 *
 * @author Jonas Lang (comments)
 */
public class Tool {

    private Point3D coordinate;
    private double rotationX, rotationY, rotationZ, rotationR;
    private double valid;
    private double timestamp;
    private String name;

    // Set Method for match Values

    /**
     * This is the set method for all tool values. There is only one set
     * method, because the values of a tool will be always set together
     *
     * @param timestamp    the recording time of the tool values
     * @param valid        shows if the tool is active at the moment
     * @param coordinate_x the X axis Coordinate of the tool
     * @param coordinate_y the Y axis Coordinate of the tool
     * @param coordinate_z the Z axis Coordinate of the tool
     * @param rotation_x   the Quaternion rotation around the X-axis
     * @param rotation_y   the Quaternion rotation around the Y-axis
     * @param rotation_z   the Quaternion rotation around the Z-axis
     * @param rotation_r   the Quaternion rotation
     * @param name         the name of the tool
     */
    public void setData(double timestamp, double valid, double coordinate_x, double coordinate_y, double coordinate_z,
                        double rotation_x, double rotation_y, double rotation_z, double rotation_r, String name) {

        this.timestamp = timestamp;
        this.valid = valid;

        coordinate = new Point3D(coordinate_x, coordinate_y, coordinate_z);

        this.rotationX = rotation_x;
        this.rotationY = rotation_y;
        this.rotationZ = rotation_z;
        this.rotationR = rotation_r;
        this.name = name;
    }

    /**
     * This returns the current timestamp of this tool
     *
     * @return the tool's timestamp
     */
    public double getTimestamp() {
        return timestamp;
    }

    /**
     * This returns the current activity status of this tool
     *
     * @return the tool's valid
     */
    public double getValid() {
        return valid;
    }

    /**
     * This returns the current coordinates of this tool as an 3D Point
     *
     * @return the tool's coordinates
     */
    public Point3D getCoordinate() {
        return coordinate;
    }

    /**
     * This returns the current Quaternion rotation around the X-axis of this
     * tool
     *
     * @return the tool's rotation_x
     */
    public double getRotationX() {
        return rotationX;
    }

    /**
     * This returns the current Quaternion rotation around the Y-axis of this
     * tool
     *
     * @return the tool's rotation_y
     */
    public double getRotationY() {
        return rotationY;
    }

    /**
     * This returns the current Quaternion rotation around the Z-axis of this
     * tool
     *
     * @return the tool's rotation_z
     */
    public double getRotationZ() {
        return rotationZ;
    }

    /**
     * This returns the current Quaternion rotation of this tool
     *
     * @return the tool's rotation_r
     */
    public double getRotationR() {
        return rotationR;
    }

    /**
     * This returns the name of the current tool
     *
     * @return the tool's name
     */
    public String getName() {
        return name;
    }

    /**
     * Compares two tools against one another for equality
     * @param other The object to compare against
     * @return If the two objects are the same
     */
    public boolean equals(final Tool other) {
        return this.name.equals(other.name)
                && this.coordinate.equals(other.coordinate)
                && this.rotationX - other.rotationX < 1e-6
                && this.rotationY - other.rotationY < 1e-6
                && this.rotationZ - other.rotationZ < 1e-6
                && this.rotationR - other.rotationR < 1e-6
                && this.timestamp - other.timestamp < 1e-6;
    }
}

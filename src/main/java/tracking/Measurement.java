package tracking;

import util.Quaternion;
import util.Vector3D;

import java.util.Objects;

public class Measurement {
    private final Vector3D position;
    private final Quaternion orientation;
    private final String toolName;
    private final double timeStamp;

    public Measurement(String toolName, double timeStamp, Vector3D position, Quaternion orientation) {
        this.position = position;
        this.orientation = orientation;
        this.toolName = toolName;
        this.timeStamp = timeStamp;
    }

    public Vector3D getPosition(){
        return position;
    }
    public Quaternion getOrientation(){
        return orientation;
    }
    public String getToolName(){
        return toolName;
    }
    public double getTimestamp(){
        return timeStamp;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Measurement that)) return false;

        return position.equals(that.position) && orientation.equals(that.orientation) && toolName.equals(that.toolName) && Double.compare(timeStamp,that.timeStamp) == 0;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(position);
        result = 31 * result + Objects.hashCode(orientation);
        result = 31 * result + Objects.hashCode(toolName);
        result = 31 * result + Objects.hashCode(timeStamp);
        return result;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "position=" + position +
                ", orientation=" + orientation +
                ", toolName='" + toolName + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}

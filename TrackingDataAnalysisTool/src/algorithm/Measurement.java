package algorithm;

import org.apache.commons.math3.complex.Quaternion;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;

import inputOutput.Tool;
import javafx.geometry.Point3D;

public class Measurement {

	public Measurement(Tool tool) {
		// this.setPoint(tool.getPoint);
		this.setPoint(new Point3D(tool.getCoordinate_x(), tool.getCoordinate_y(), tool.getCoordinate_z()));
		// this.setRotation(new Rotation(tool.getRotation_r(), tool.getRotation_x(),
		// tool.getRotation_y(), tool.getRotation_z()));
		this.setTimestamp(tool.getTimestamp());
		this.setToolname(tool.getName());

	}

	public Measurement() {

	}

	private Point3D point;
	private double error;
	private double timestamp;
	private String toolname;
	private Rotation rotation;

	public Rotation getRotation() {
		return rotation;
	}

	public void setRotation(Rotation rotation) {
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

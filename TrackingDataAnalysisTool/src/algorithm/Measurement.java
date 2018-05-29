package algorithm;

import org.apache.commons.math3.complex.Quaternion;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;

import inputOutput.Tool;
import javafx.geometry.Point3D;

public class Measurement {

	// public Measurement(Tool tool) {
	// //this.setPoint(tool.getPoint())
	// }

	private Point3D point;
	private double error;
	private int timestamp;
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

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
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

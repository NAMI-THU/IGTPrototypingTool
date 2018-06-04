package inputOutput;

import javafx.geometry.Point3D;

public class Tool {

	private double rotation_x, rotation_y, rotation_z, rotation_r;

	private double valid;

	private double timestamp;

	private String name;

	Point3D coordinate;

	// Set Method for match Values
	public void setData(double timestamp, double valid, double coordinate_x, double coordinate_y, double coordinate_z,
			double rotation_x, double rotation_y, double rotation_z, double rotation_r, String name) {

		this.timestamp = timestamp;
		this.valid = valid;

		coordinate = new Point3D(coordinate_x, coordinate_y, coordinate_z);

		this.rotation_x = rotation_x;
		this.rotation_y = rotation_y;
		this.rotation_z = rotation_z;
		this.rotation_r = rotation_r;
		this.name = name;

	}

	// Get-Method for getting the separate Values
	public double getTimestamp() {
		return timestamp;
	}

	public double getValid() {
		return valid;
	}

	public Point3D getCoordinat() {
		return coordinate;

	}

	public double getRotation_x() {
		return rotation_x;
	}

	public double getRotation_y() {
		return rotation_y;
	}

	public double getRotation_z() {
		return rotation_z;
	}

	public double getRotation_r() {
		return rotation_r;
	}

	public String getName() {
		return name;
	}
	

}

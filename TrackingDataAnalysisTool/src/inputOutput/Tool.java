package inputOutput;

public class Tool {
	
	private double coordinate_x;
	
	

	private double coordinate_x, coordinate_y, coordinate_z;

	private double rotation_x, rotation_y, rotation_z, rotation_r;

	private double valid;

	private double timestamp;

	private String name;

	// Set Method for match Values
	public void setData(double timestamp, double valid, double coordinate_x, double coordinate_y, double coordinate_z,
			double rotation_x, double rotation_y, double rotation_z, double rotation_r, String name) {

		this.timestamp = timestamp;
		this.valid = valid;
		this.coordinate_x = coordinate_x;
		this.coordinate_y = coordinate_y;
		this.coordinate_z = coordinate_z;
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

	public double getCoordinate_x() {
		return coordinate_x;
	}

	public double getCoordinate_y() {
		return coordinate_y;
	}

	public double getCoordinate_z() {
		return coordinate_z;
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

	// Test output for correct matching
	@Override
	public String toString() {
		return "Tool [coordinate_x=" + coordinate_x + ", coordinate_y=" + coordinate_y + ", coordinate_z="
				+ coordinate_z + ", rotation_x=" + rotation_x + ", rotation_y=" + rotation_y + ", rotation_z="
				+ rotation_z + ", rotation_r=" + rotation_r + ", valid=" + valid + ", timestamp=" + timestamp
				+ ", name=" + name + "]";
	}

}

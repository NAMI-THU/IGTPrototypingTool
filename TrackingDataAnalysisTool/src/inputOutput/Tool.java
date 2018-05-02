package inputOutput;

public class Tool {

	private double coordinate_x, coordinate_y, coordinate_z;

	private double rotation_x, rotation_y, rotation_z, rotation_r;

	private int valid;

	private int timestamp;

	private String name;

	public Tool(int timestamp, int valid, double coordinate_x, double coordinate_y, double coordinate_z,
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

}

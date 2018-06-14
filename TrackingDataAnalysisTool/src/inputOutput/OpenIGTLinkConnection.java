package inputOutput;

import java.util.ArrayList;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.sun.j3d.utils.geometry.compression.CompressedGeometryData.Header;

public class OpenIGTLinkConnection {

	private static double coordinate_x;
	private static double coordinate_y;
	private static double coordinate_z;
	private static double rotation_r;
	private static double rotation_x;
	private static double rotation_y;
	private static double rotation_z;
	private static String name;
	private static double timestamp;
	private static double valid = 1;
	private static ArrayList<Tool> toollist = new ArrayList<Tool>();
	private static int counter = 0;

	public static ArrayList<Tool> update() {

		if (counter == 0) {
			init();
			counter++;
		}

		return toollist;

	}

	public static void init() {

		Networkconnection.Connection();

	}

	public void setValues(String n, TransformNR t) {

		timestamp = org.medcare.igtl.util.Header.getTimeStamp();

		name = n;
		coordinate_x = t.getX();
		coordinate_y = t.getY();
		coordinate_z = t.getZ();

		rotation_r = t.getRotation().getRotationMatrix2QuaturnionW();
		rotation_x = t.getRotation().getRotationMatrix2QuaturnionX();
		rotation_y = t.getRotation().getRotationMatrix2QuaturnionY();
		rotation_z = t.getRotation().getRotationMatrix2QuaturnionZ();

//		Tool tool = new Tool();
//
//		tool.setData(timestamp, valid, coordinate_x, coordinate_y, coordinate_z, rotation_x, rotation_y, rotation_z,
//				rotation_r, name);
//
//		setArraylist(tool);

		System.out.println(name);
		System.out.println(timestamp);

		System.out.println(coordinate_x);
		System.out.println(coordinate_y);
		System.out.println(coordinate_z);
		System.out.println(rotation_r);
		System.out.println(rotation_x);
		System.out.println(rotation_y);
		System.out.println(rotation_z);

	}

	public void setArraylist(Tool tool) {

		toollist.add(tool);

	}

}

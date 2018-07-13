package inputOutput;

import java.util.ArrayList;
/**
 * this class makes it possible to connect with OpenIGTLink so the tracking data of the tools (coordinates, rotation, name, etc.)
 * can be transfered directly without saving it in a CSV-file in advance
 */

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.sun.j3d.utils.geometry.compression.CompressedGeometryData.Header;

public class OpenIGTLinkConnection {

	private double coordinate_x;
	private double coordinate_y;
	private double coordinate_z;
	private double rotation_r;
	private double rotation_x;
	private double rotation_y;
	private double rotation_z;
	private String name;
	private double timestamp;
	private double valid = 1;
	private ArrayList<Tool> toollist = new ArrayList<Tool>();
	private int counter = 0;
	private int counter1 = 1;
	private String helpname;
	private static boolean testapp = false;

	// public static void update() {
	//
	// Networkconnection.Connection();
	//
	// }

	
	/**
	 * this method assigns the values of each tool to an object of a toollist
	 * @param n is the name of the tool
	 * @param t is a matrix which tells us the coordinates and the rotation of each tool
	 */
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

		if (counter1 == 1) {

			helpname = name;

		}

		Tool tool = new Tool();

		tool.setData(timestamp, valid, coordinate_x, coordinate_y,
				coordinate_z, rotation_x, rotation_y, rotation_z, rotation_r,
				name);

		setArraylist(tool);

		counter1++;

	}
	
	
	/**
	 * this method creates an ArrayList for the data of the tools containing as many objects as there are tools available
	 * @param tool contains the values of one tool
	 */

	public void setArraylist(Tool tool) {

		if (helpname.equals(name) && counter1 != 1) {

			// set toollist in DataManager from group2
			algorithm.DataManager data = new algorithm.DataManager();
			data.setList(toollist);

			// call method to get output in console
			if (testapp == true) {
				testInputOutput.Networkconnection_test_app
						.setTestlist(toollist);
			}

			toollist.clear();
			counter1 = 1;
		}

		toollist.add(tool);

	}

	//use for Networkconnection_test_app
	public static void setTestappValue() {
		testapp = true;
	}

}

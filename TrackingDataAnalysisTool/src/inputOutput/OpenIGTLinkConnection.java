package inputOutput;

import java.util.ArrayList;
/**
 * this class makes it possible to connect with OpenIGTLink so the tracking data of the tools (coordinates, rotation, name, etc.)
 * can be transfered directly without saving it in a CSV-file in advance
 */



import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.Log;
import com.sun.j3d.utils.geometry.compression.CompressedGeometryData.Header;

public class OpenIGTLinkConnection extends TrackingDataSource{

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
	private int counter1 = 1;
	private String helpname;
	private static boolean testapp = false;

	
	private String ipAddress = "127.0.0.1";
	private int port = 18944;
	private Networkconnection myOpenIGTLinkConnection;

	@Override
	public ArrayList<Tool> update() {
		if (myOpenIGTLinkConnection == null) {
			myOpenIGTLinkConnection = new Networkconnection();
			myOpenIGTLinkConnection.Connect(ipAddress, port);
			toollist = new ArrayList<Tool>();
		}
		List<Networkconnection.ToolData> rawToolList = myOpenIGTLinkConnection.getToolDataList();
		synchronized (rawToolList) {
			for(Networkconnection.ToolData t : rawToolList) this.setValues(t.name, t.t);
		}
		
		return toollist;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}


	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}


	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}
	
	


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

		for (Tool cur_tool : toollist)
		{
			if(cur_tool.getName().equals(n)) 
				{
				cur_tool.setData(timestamp, valid, coordinate_x, coordinate_y,
					coordinate_z, rotation_x, rotation_y, rotation_z, rotation_r,
					name);
				return;	
				}
		}
		

		Tool newTool = new Tool();
		newTool.setData(timestamp, valid, coordinate_x, coordinate_y,
				coordinate_z, rotation_x, rotation_y, rotation_z, rotation_r,
				name);
		this.toollist.add(newTool);
		
	}
	
	public void closeConnection()
	{
		myOpenIGTLinkConnection.setExit(false);
	}
	
}

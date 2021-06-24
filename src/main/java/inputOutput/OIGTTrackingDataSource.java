
package inputOutput;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

import java.util.ArrayList;
import java.util.List;

/**
 * this class makes it possible to connect with OpenIGTLink so the tracking data of the tools (coordinates, rotation, name, etc.)
 * can be transfered directly without saving it in a CSV-file in advance
 */

public class OIGTTrackingDataSource extends AbstractTrackingDataSource {

    private OpenIGTLinkConnection myOpenIGTLinkConnection;
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

    @Override
    public ArrayList<Tool> update() {
        if (myOpenIGTLinkConnection == null) {
            myOpenIGTLinkConnection = new OpenIGTLinkConnection();
            toolList = new ArrayList<Tool>();
        }
        List<OpenIGTLinkConnection.ToolData> rawToolList = myOpenIGTLinkConnection.getToolDataList();
        synchronized (rawToolList) {
            for (OpenIGTLinkConnection.ToolData t : rawToolList)
                this.setValues(t.name, t.t);
        }

        return toolList;
    }

    /**
     * this method assigns the values of each tool to an object of a toollist
     *
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

        for (Tool cur_tool : toolList) {
            if (cur_tool.getName().equals(n)) {
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
        this.toolList.add(newTool);

    }

    public void closeConnection() {
        myOpenIGTLinkConnection.stop();
    }

}

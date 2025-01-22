package tracking.tracker;

import org.medcare.igtl.util.Header;
import tracking.Measurement;
import util.Quaternion;
import util.Vector3D;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * this class makes it possible to connect with OpenIGTLink so the tracking data of the tools (coordinates, rotation, name, etc.)
 * can be transfered directly without saving it in a CSV-file in advance
 */

public class OIGTTrackingDataSource implements TrackingSource {

    private OpenIGTLinkConnection oigtlConnection;

    @Override
    public Map<String, Measurement> measure() {
        if(oigtlConnection == null){
            connect();
        }

        var map = new HashMap<String, Measurement>();

        List<OpenIGTLinkConnection.ToolData> rawToolList = oigtlConnection.getToolDataList();
        for(var dataItem : rawToolList){
            map.put(dataItem.name,
                    new Measurement(
                            dataItem.name,
                            Header.getTimeStamp(),
                            new Vector3D(dataItem.t.getX(),dataItem.t.getY(),dataItem.t.getZ()),
                            new Quaternion(dataItem.t.getRotation().getW(), dataItem.t.getRotation().getX(), dataItem.t.getRotation().getY(), dataItem.t.getRotation().getZ())
                    ));
//                    new Measurement(
//                            dataItem.name,
//                            org.medcare.igtl.util.Header.getTimeStamp(),
//                            dataItem.t.getX(),
//                            dataItem.t.getY(),
//                            dataItem.t.getZ(),
//                            dataItem.t.getRotation().getX(),
//                            dataItem.t.getRotation().getY(),
//                            dataItem.t.getRotation().getZ(),
//                            dataItem.t.getRotation().getW()));
        }
        return map;
    }

    @Override
    public void connect(){
        if (oigtlConnection == null) {
            oigtlConnection = new OpenIGTLinkConnection();
        }
    }

    @Override
    public void disconnect() {
        oigtlConnection.stop();
    }

    @Override
    public boolean isConnected() {
        return oigtlConnection != null && oigtlConnection.isConnected();
    }

}

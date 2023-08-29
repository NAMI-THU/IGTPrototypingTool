package sample;

import Jama.Matrix;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import org.medcare.igtl.messages.ImageMessage;
import org.medcare.igtl.network.GenericIGTLinkClient;
import org.medcare.igtl.network.IOpenIgtPacketListener;
import org.medcare.igtl.util.Status;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientSample implements IOpenIgtPacketListener {
    static Logger logger = Logger.getLogger(ClientSample.class.getName());
    /**
     * @param args
     */
    public static void main(String[] args) {
        String msg = "<Command Name=\"SomeCommandName\" SomeAttribute1=\"attribute value 1\" SomeAttribute2=\"123\"><Param name=\"Param1\"/><Param name=\"Param2\"/></Command>";

        ClientSample.parseXMLStringMessage(msg);
        GenericIGTLinkClient client;
        try {
            logger.log(Level.FINE, "Connecting to server");
            client = new GenericIGTLinkClient("127.0.0.1", 18944);
            client.addIOpenIgtOnPacket(new ClientSample());

            for (int i = 0; i < 50; i++) {
                Thread.sleep(1000);
            }
            client.stopClient();
            logger.log(Level.FINE, "Client disconnected");
            System.exit(0);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void parseXMLStringMessage(String msg) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onRxTransform(String name, TransformNR t) {
        //Log.debug("Received Transform: "+t);
        if (name.equals("RegistrationTransform") || name.equals("CALIBRATION")) {
            System.err.println("Received Registration Transform");
            logger.log(Level.FINE, "Setting fiducial registration matrix: " + t);
            return;
        } else if (name.equals("TARGET")) {
            System.err.println("Received RAS Transform: TARGET");
            logger.log(Level.FINE, "Setting task space pose: " + t);

        } else if (name.equals("myTransform")) {
            System.err.println("Received Transformation Matrix: myTransform");
            logger.log(Level.FINE, "Setting task space pose: " + t);

        } else {
            System.err.println("Received unidentified transform matrix");
            logger.log(Level.FINE, "Setting task space pose: " + t);
        }
    }

    @Override
    public TransformNR getTxTransform(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Status onGetStatus(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onRxString(String name, String body) {
    }

    @Override
    public String onTxString(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onRxDataArray(String name, Matrix data) {
        // TODO Auto-generated method stub

    }

    @Override
    public double[] onTxDataArray(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onRxImage(String name, ImageMessage image) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTxNDArray(String name) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRxNDArray(String name, float[] data) {
        // TODO Auto-generated method stub
        logger.log(Level.FINE, "Received RxNDArray: " + name);
        for (int i = 0; i < data.length; i++) {
            logger.log(Level.FINE, "Data[" + i + "]=" + (double) data[i]);
        }
    }
}

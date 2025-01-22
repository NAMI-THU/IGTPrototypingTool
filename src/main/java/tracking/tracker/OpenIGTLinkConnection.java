package tracking.tracker;

import org.medcare.igtl.messages.ImageMessage;
import org.medcare.igtl.network.GenericIGTLinkClient;
import org.medcare.igtl.network.IOpenIgtPacketListener;
import org.medcare.igtl.util.Status;
import org.medcare.igtl.util.TransformNR;
import org.medcare.igtl.util.Matrix3D;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * builds a connection based on OpenIGTLink for tracking data and image data.
 * As a prerequisite MITK v2016.11 has to run in the background.
 * The connection is established by using the online stream.
 * @author team3
 */
public class OpenIGTLinkConnection implements IOpenIgtPacketListener {

    private GenericIGTLinkClient client;
    private String ip = "127.0.0.1";
    private int port = 18944;
    private ImageMessage imgMsg;
    private String name;
    private byte[] imgData;
    private long time1;
    private long time2;
    private long timeRes;
    public long fps;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private List<ToolData> toolDataList = Collections.synchronizedList(new LinkedList<>());
    private final boolean exit = true;
    private final boolean stop = true;

    /**
     * This constructor establishes a connection to a OpenIGTLink server using the default ip-address and port.
     */
    public OpenIGTLinkConnection() {
        try {
            logger.log(Level.INFO, "Starting OIGTL client");
            this.client = new GenericIGTLinkClient(this.ip, this.port);

            this.client.addIOpenIgtOnPacket(this);
            this.toolDataList = Collections.synchronizedList(new LinkedList<>());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error establishing connection to IGTLink server.", e);
        }
        this.time1 = System.currentTimeMillis();
    }

    /**
     * An IP-address and a port number is needed for the connection. Here both are set through the constructor.
     * @param ip
     * @param port
     */
    public OpenIGTLinkConnection(String ip, int port) {

        this.setIp(ip);
        this.setPort(port);

        try {
            logger.log(Level.INFO, "Starting OIGTL client");
            this.client = new GenericIGTLinkClient(this.ip, this.port);

            client.addIOpenIgtOnPacket(this);
            this.toolDataList = Collections.synchronizedList(new LinkedList<>());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error establishing connection to IGTLink server.", e);
        }
        this.time1 = System.currentTimeMillis();
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * This method is receiving an image as ImageMessage from the server, while it is running in the background during the connection.
     * After getting an ImageMessage, the <code>setImageMessage(ImageMessage img)</code> method is called in order to set the received ImageMessage and the body of ImageMessage is unpacked in order to get the image data, which is also set by calling <code>setImageData(byte[] imgData)</code>.
     * The framerate is also determined here.
     */
    @Override
    public void onRxImage(String name, ImageMessage image) {

        setImageMessage(image);

        try {
//    image.UnpackBody();
            setImageDataByte(image.getImageData());

        } catch (Exception e) {
            e.printStackTrace();
        }
        this.name = name;
        this.time2 = System.currentTimeMillis();
        this.timeRes = this.time2 - this.time1;
        this.time1= System.currentTimeMillis();
        this.fps = 1000 / this.timeRes;
    }

    /**
     * This method is for getting the current ImageMessage.
     * @return ImageMessage
     */
    public ImageMessage getImageMessage() {
        return this.imgMsg;
    }

    /**
     * This method is for setting an ImageMessage.
     */
    public void setImageMessage(ImageMessage img) {
        this.imgMsg = img;
    }

    /**
     * This method is for setting an image data in the form of a byte.
     */
    public void setImageDataByte(byte[] imgData) {
        this.imgData = imgData;
    }

    /**
     * This method is for getting the current image data.
     * @return It returns the image data as a byte[]
     */
    public byte[] getImageDataByte() {
        return this.imgData;
    }

    /**
     * This method is for stopping the connection (the client).
     * @return <code>client.isInterrupted()</code> is true if closing the connection was successful
     */
    public boolean stop() {
        this.client.stopClient();
        return this.client.isInterrupted();
    }

    /**
     * This method is for checking the connection while the application is running.
     * @return <code>isConnected</code>
     */
    public boolean isConnected() {
        return this.client.isConnected();
    }

    // from networkconnection
    @Override
    public void onRxTransform(String name, TransformNR t) {
        logger.log(Level.FINE, "Received Transform: " + t);

        if (this.exit && this.stop) {

            boolean foundTool = false;

            synchronized (this.toolDataList) {
                for (ToolData d : this.toolDataList) {
                    if (d.name.equals(name)) {
                        d.t = t;
                        foundTool = true;
                    }
                }

                if (!foundTool) {
                    ToolData newData = new ToolData();
                    newData.name = name;
                    newData.t = t;
                    this.toolDataList.add(newData);
                }
            }


        } else if (this.exit == false) {
            this.client.stopClient();
        }

        if (name.equals("RegistrationTransform") || name.equals("CALIBRATION")) {
            // System.err.println("Received Registration Transform");
            logger.log(Level.FINE, "Setting fiducial registration matrix: " + t);
            return;
        } else if (name.equals("TARGET")) {
            // System.err.println("Received RAS Transform: TARGET");
            logger.log(Level.FINE, "Setting task space pose: " + t);

        } else if (name.equals("myTransform")) {
            // System.err.println("Received Transformation Matrix: myTransform");
            logger.log(Level.FINE, "Setting task space pose: " + t);

        } else {
            // System.err.println("Received unidentified transform matrix");
            logger.log(Level.FINE, "Setting task space pose: " + t);
        }
    }

    public List<ToolData> getToolDataList() {
        return this.toolDataList;
    }

    public static class ToolData {
        String name;
        TransformNR t;
    }

    public static void parseXMLStringMessage(String msg) {
        String msg1 = "<Command Name=\"SomeCommandName\" SomeAttribute1=\"attribute value 1\" SomeAttribute2=\"123\"><Param name=\"Param1\"/><Param name=\"Param2\"/></Command>";
    }


    /**
     * The methods below are not used during a connection over MITK
     */
    @Override
    public TransformNR getTxTransform(String name) {
        return null;
    }

    @Override
    public Status onGetStatus(String name) {
        return null;
    }

    @Override
    public void onRxString(String name, String body) {
    }

    @Override
    public String onTxString(String name) {
        return null;
    }


    @Override
    public void onRxDataArray(String name, Matrix3D data) {
    }

    @Override
    public double[] onTxDataArray(String name) {
        return null;
    }

    @Override
    public void onTxNDArray(String name) {
    }

    @Override
    public void onRxNDArray(String name, float[] data) {
    }

}

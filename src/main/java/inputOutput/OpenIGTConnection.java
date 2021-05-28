package inputOutput;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import Jama.Matrix;
import org.medcare.igtl.messages.ImageMessage;
import org.medcare.igtl.network.GenericIGTLinkClient;
import org.medcare.igtl.network.IOpenIgtPacketListener;
import org.medcare.igtl.util.Status;

/**
 * builds a connection based on OpenIGTLink. As a prerequisite MITK v2016.11 has to run in the background.
 * The connection is established by using the online stream.
 * @author team3
 *
 */
public class OpenIGTConnection implements IOpenIgtPacketListener {
    private GenericIGTLinkClient client;
    private ImageMessage imgMsg;
    private String name;
    private byte[] imgData;
    private long time1;
    private long time2;
    private long timeRes;
    public long fps;


    /**
     * An IP-address and a port number is needed for the connection. Here both are set through the constructor.
     * @param ip
     * @param port
     */
    public OpenIGTConnection(String ip, int port) {

        try {
            client = new GenericIGTLinkClient(ip, port);

            client.addIOpenIgtOnPacket(this);
        } catch (Exception e) {

            e.printStackTrace();
        }
        time1 = System.currentTimeMillis();
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
//			image.UnpackBody();
            setImageDataByte(image.getImageData());

        } catch (Exception e) {
            e.printStackTrace();
        }
        this.name = name;
        time2 = System.currentTimeMillis();
        timeRes = time2-time1;
        time1= System.currentTimeMillis();
        fps = 1000/timeRes;
	}

    /**
     * This method is for getting the current ImageMessage.
     * @return ImageMessage
     */
    public ImageMessage getImageMessage() {
        return imgMsg;
    }

    /**
     * This method is for setting an ImageMessage.
     */
    public void setImageMessage(ImageMessage img) {
        imgMsg = img;
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
        return imgData;
    }

    /**
     * This method is for stopping the connection (the client).
     * @return <code>client.isInterrupted()</code> is true if closing the connection was successful
     */
    public boolean stop() {
        client.stopClient();
        return client.isInterrupted();
    }

    /**
     * This method is for checking the connection while the application is running.
     * @return <code>isConnected</code>
     */
    public boolean isConnected() {
        return client.isConnected();
    }

    /**
     * The methods below are not used during a connection over MITK
     */
    @Override
    public void onRxTransform(String name, TransformNR t) {
    }

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
    public void onRxDataArray(String name, Matrix data) {

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

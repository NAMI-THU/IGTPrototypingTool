package inputOutput;


import org.opencv.core.Mat;

import nu.pattern.OpenCV;

import org.medcare.igtl.messages.ImageMessage;

/**
 * provides the basic functionalities for the application like opening and closing the connection, and an image converted to a Mat object from OpenCV.
 * The class OpenIGTLinkConnection is needed here for establishing the connection.
 *
 * @author team3
 *
 */
public class OIGTImageSource extends AbstractImageSource {

    private OpenIGTLinkConnection igtConnection;
    private ImageMessage imgMsg;
    private byte[] imgData;
    private int rows;
    private int cols;
    private int type;

    /**
     * This constructor is used when the user doesn't enter an ip address or a port number.
     * The default value are used then.
     */
    public OIGTImageSource() {
        OpenCV.loadLocally();
    }

    /**
     * This method is starting the connection by creating an object of OpenIGTConnection. A </code>ip</code> address and <code>port</code> number is necessary. <code>Thread.sleep</code> is needed here to wait (2 seconds) for the connection to establish.
     * The height and width of the image adapts here to the given dimensions from MITK by usig the <code>getDimensions</code> method from the OpenIGT library. The parameter 0 returns the width, 1 returns the height and 2 returns the type.
     *
     * @return whether the connection was successful or not
     */
    public boolean openConnection() {

        igtConnection = new OpenIGTLinkConnection();
        isConnected = true;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        imgMsg = igtConnection.getImageMessage();
        rows = (int) imgMsg.getDimensions()[1];
        cols = (int) imgMsg.getDimensions()[0];
        type = (int) imgMsg.getDimensions()[2];

        frameMatrix = new Mat (rows, cols, type);

        return isConnected;
    }

    /**
     * This method is for closing the connection. It calls the <code>stop()</code method from OpenIGTConnection
     *
     * @return <code>exit</code> is true if closing the connection was successful
     */
    public boolean closeConnection() {
        igtConnection.stop();
        isConnected = false;
        exit = true;
        return exit;
    }

    /**
     * This method is getting the current ImageMessage if it's needed.
     *
     * @return <code>imgMsg</code>
     */
    public ImageMessage getImageMessage() {
        imgMsg = igtConnection.getImageMessage();
        return imgMsg;
    }

    /**
     * This method is getting the <code>imgData</code> as byte array (with a length of rows*cols), and then it is converted to a Mat object from OpenCV by using <code>put(int row, int col, byte[])</code>.
     *
     * @return Mat (from the OpenCV library)
     */
    public Mat getNextMat() {
        imgData = igtConnection.getImageDataByte();
        frameMatrix.put(0, 0, imgData);

        fps = (int) igtConnection.fps;
        return frameMatrix;
    }

    /**
     * This method is for checking the connection while the application is running by calling the <code>isConnected()</code> method from OpenIGTConnection.
     * @return <code>isConnected</code>
     */
    public boolean checkConnection() {
        isConnected = igtConnection.isConnected();

        return isConnected;
    }

    /**
     * This method returns information about the type of ImageMessage.
     *
     * @return the scalar type as a long
     */
    public long getImageScalarType() {
        return imgMsg.getScalarType();
    }

    /**
     * This method returns information about spacing of ImageMessage.
     *
     * @return spacing as double[]
     */
    public double[] getSpacing() {
        return imgMsg.getSpacing();
    }

    /**
     * This method returns information about the origin of ImageMessage.
     *
     * @return origin as double[]
     */
    public double[] getOrigin() {
        return imgMsg.getOrigin();
    }

    /**
     * This method returns the frames per second.
     *
     * @return <code>fps</code> as a int
     */
    public int getFPS() {
        return fps;
    }
}

package inputOutput;

import org.opencv.core.Mat;

/**
 * builds an interface to other teams and enables to get a Matrix,
 * establishes a connection to the device and disconnects the
 * connection
 *
 * @author team3
 *
 */
public abstract class AbstractImageSource {

    public boolean isConnected = false;
    public boolean exit;
    public Mat frameMatrix;
    public int fps;

    /**
     * provides the next Matrix for a further use
     * @return the object of the class Mat from OpenCV
     */
    public abstract Mat getNextMat();

    /**
     * establishes a connection to the device
     * @return whether the connection was successful or not
     */
    public abstract boolean openConnection();

    /**
     * disconnects the connection to the device
     * @return <code>exit = true</code>
     */
    public abstract boolean closeConnection();

}

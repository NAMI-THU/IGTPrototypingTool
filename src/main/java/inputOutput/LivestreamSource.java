package inputOutput;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import nu.pattern.OpenCV;

/**
 * provides the livestream footage from a webcam, ultrasound device or any other
 * suitable devices.
 *
 * @author Team 3
 *
 */
public class LivestreamSource extends AbstractImageSource {

    private VideoCapture vc;
    private int deviceID = 0;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * constructs a new LivestreamSource object with the transmitted
     * <code>id</code>.
     *
     * @param id  describes which device is used
     */
    public LivestreamSource(int id) {
        OpenCV.loadLocally();
        frameMatrix = new Mat();
        deviceID = id;
    }

    /**
     * initializes a new VideoCapture object using the <code>deviceID</code>. The
     * webcam (or other video devices) is opened.
     *
     * @return whether the connection was successful or not
     */
    public boolean openConnection() {

        vc = new VideoCapture(deviceID);

        if (vc.isOpened()) {
            logger.log(Level.INFO,"found VideoSource " + vc.toString());
            isConnected = true;
        } else {
            logger.log(Level.WARNING,"!!! Did not connect to camera !!!");
            isConnected = false;
        }

        return isConnected;
    }

    /**
     * Transmits the matrix of the picture
     *
     * @return <code>frameMatrix</code>
     */
    public Mat getNextMat() {
        fps = (int) vc.get(Videoio.CAP_PROP_FPS);
        vc.read(frameMatrix);

        if (frameMatrix.empty()) {
            logger.log(Level.WARNING,"!!! Nothing captured from webcam !!!");
        }

        return frameMatrix;
    }

    /**
     * closes connection to the device and sets <code>isConnected = false</code>
     * @return <code>isConnected = false</code>
     */
    public boolean closeConnection() {

        vc.release();
        HighGui.destroyAllWindows();
        exit = true;
        isConnected = false;

        return exit;
    }

}

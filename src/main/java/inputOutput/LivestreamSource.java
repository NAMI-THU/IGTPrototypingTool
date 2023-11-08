package inputOutput;

import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import static org.opencv.imgproc.Imgproc.resize;

/**
 * provides the livestream footage from a webcam, ultrasound device or any other
 * suitable devices.
 *
 * @author Team 3
 *
 */
public class LivestreamSource extends AbstractImageSource {
    private final int desiredWidth = 640;
    private final int desiredHeight = 480;
    private VideoCapture vc;
    private int deviceID = 0;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private static Map<Integer, LivestreamSource> instances = new TreeMap<>();

    private static final Preferences userPreferencesGlobal = Preferences.userRoot().node("IGT_Settings");
    public static LivestreamSource forDevice(int id) {
        if (!instances.containsKey(id)) {
            instances.put(id, new LivestreamSource(id));
        }
        return instances.get(id);
    }
    /**
     * constructs a new LivestreamSource object with the transmitted
     * <code>id</code>.
     *
     * @param id  describes which device is used
     */
    private LivestreamSource(int id) {
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
        if (vc != null && vc.isOpened()) {
            logger.log(Level.INFO,"VideoCapture already initialized, reusing it.");
            return true;
        }

        vc = new VideoCapture(deviceID);
        // This will set the resolution to the highest possible
        vc.set(Videoio.CAP_PROP_FRAME_HEIGHT, desiredHeight);
        vc.set(Videoio.CAP_PROP_FRAME_WIDTH, desiredWidth);

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

        resize(frameMatrix, frameMatrix, new Size(desiredWidth,desiredHeight));

        return frameMatrix;
    }

    /**
     * closes connection to the device and sets <code>isConnected = false</code>
     * @return <code>isConnected = false</code>
     */
    public boolean closeConnection() {
        if(vc != null) {
            vc.release();
        }
        HighGui.destroyAllWindows();
        exit = true;
        isConnected = false;

        return exit;
    }

}

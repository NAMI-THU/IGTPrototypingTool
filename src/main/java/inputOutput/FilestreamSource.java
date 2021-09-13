package inputOutput;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import nu.pattern.OpenCV;

/**
 * provides the footage from a file with these formats: mp4, avi, mkv, mov, 3GP, mpg,
 * jpg and png
 *
 * @author Team 3
 */
public class FilestreamSource extends AbstractImageSource {

    private VideoCapture vc;
    private String path;
    private int frameTotalNumber;
    private int currentFrame;
    private boolean repeatMode = true;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * constructs a new FilestreamSource object with the transmitted
     * <code>path</code>.
     *
     * @param path
     *            describes the path of the file
     */
    public FilestreamSource(String path) {
        OpenCV.loadLocally();
        frameMatrix = new Mat();
        this.path = path;
    }

    /**
     * initializes a new VideoCapture object using the <code>path</code>. The video
     * or image is opened.
     *
     * @return whether the connection was successful or not
     */
    public boolean openConnection() {
        vc = new VideoCapture(path);

        if (vc.isOpened()) {
            logger.log(Level.INFO, "found VideoSource " + vc.toString());
            isConnected = true;
            frameTotalNumber = (int) vc.get(Videoio.CAP_PROP_FRAME_COUNT);
        } else {
            logger.log(Level.WARNING, "!!! Did not find video file !!!");
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
        this.currentFrame++;

        if(repeatMode && (this.currentFrame == this.frameTotalNumber)) {
            vc.set(Videoio.CAP_PROP_POS_FRAMES, 0);
            this.currentFrame = 0;
        }

        if (frameMatrix.empty()) {
            logger.log(Level.WARNING, "!!! Nothing captured from video file !!!");
        }

        return frameMatrix;
    }


    /**
     * sets <code>isConnected = false</code>
     *
     * @return <code>isConnected = false</code>
     */
    public boolean closeConnection() {

        vc.release();
        HighGui.destroyAllWindows();
        exit = true;
        isConnected = false;

        return exit;
    }

    /**
     * returns the frame total number of the selected file.
     * @return <code>frameTotalNumber</code>
     */
    public int getTotalFrameNumber() {
        return frameTotalNumber;
    }
}

package algorithm;

import java.awt.image.BufferedImage;

import inputOutput.VideoSource;
import javafx.scene.image.Image;
import org.opencv.core.Mat;

/**
 * The Video Service is a wrapper class for the VideoDataProcessor to ease the access.
 */
public class VideoService {
    private final VideoDataProcessor dataProcessor = new VideoDataProcessor();

    public boolean openConnection(VideoSource source){
        return dataProcessor.openConnection(source);
    }

    /**
     * Opens a new connection to the given videosource. In case of a livestream camera, the deviceId reflects the camera id in your system. In case an connection to this webcam has already been opened before, the connection is reused.
     * @param source The type of video source
     * @param deviceId Only for Livestream source: Hardware-ID of the camera according to opencv
     * @return whether establishing a connection was successful.
     */
    public boolean openConnection(VideoSource source, int deviceId){
        return dataProcessor.openConnection(source, deviceId);
    }

    public boolean closeConnection(){
        return dataProcessor.closeConnection();
    }

    public BufferedImage readBufImg() {
        return dataProcessor.readBufImg();
    }

    public Image readImg() {
        return dataProcessor.readImg();
    }

    public Mat readMat(){return dataProcessor.readMat();}

    public VideoDataProcessor getDataProcessor() {
        return this.dataProcessor;
    }
}

package algorithm;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.logging.Level;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import inputOutput.AbstractImageSource;
import inputOutput.FilestreamSource;
import inputOutput.LivestreamSource;
import inputOutput.OIGTImageSource;
import javafx.scene.image.Image;
import util.CustomLogger;

/**
 * ImageDataProcessor accesses AbstractImageSource to control the connection and read images.
 *
 */
public class ImageDataProcessor {

    AbstractImageSource imgSrc;
    String filePath;

    /**
     * Old method to read image data. Uses Java.awt.BufferedImage
     * @return
     */
    BufferedImage readBufImg() {
        Mat mat = imgSrc.getNextMat();
        BufferedImage bufImg = (BufferedImage) HighGui.toBufferedImage(mat);
        return bufImg;
    }

    /**
     * Reads an image from video source and converts the matrix to a JavaFx image
     * @return Image
     */
    public Image readImg() {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", imgSrc.getNextMat(), buffer);
        Image img = new Image(new ByteArrayInputStream(buffer.toArray()));
        return img;
    }

    /**
     * Sets the type of image source depending on parameter x and prepares the connection.
     * 0: LivestreamSource
     * 1: OpenIGTLinkSource
     * 2: FilestreamSource
     * @param x int
     */
    public void openConnection(int x){
        switch (x) {
        case 0:
            imgSrc = new LivestreamSource(x);
            break;
        case 1:
            imgSrc = new OIGTImageSource();
            break;
        case 2:
            imgSrc = new FilestreamSource(filePath);
            break;
        }

       if(imgSrc != null) {
            imgSrc.openConnection();
        } else {
            CustomLogger.log(Level.WARNING, "Image source could not be set.");
        }
    }

    public void closeConnection(){
        imgSrc.closeConnection();
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public boolean isConnected() {
        return imgSrc != null ? imgSrc.isConnected : false;
    }
}

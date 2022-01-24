package algorithm;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import inputOutput.AbstractImageSource;
import inputOutput.FilestreamSource;
import inputOutput.LivestreamSource;
import inputOutput.OIGTImageSource;
import javafx.scene.image.Image;

/**
 * ImageDataProcessor accesses AbstractImageSource to control the connection and read images.
 *
 */
public class ImageDataProcessor {

    AbstractImageSource imgSrc;
    String filePath;
    int topCrop = 0;
    int bottomCrop = 0;
    int leftCrop = 0;
    int rightCrop = 0;
    Logger logger = Logger.getLogger(this.getClass().getName());

    public void setTopCrop(int topCrop) {
        this.topCrop = topCrop;
    }

    public void setBottomCrop(int bottomCrop) {
        this.bottomCrop = bottomCrop;
    }

    public void setLeftCrop(int leftCrop) {
        this.leftCrop = leftCrop;
    }

    public void setRightCrop(int rightCrop) {
        this.rightCrop = rightCrop;
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
     * Get raw Mat image to process it
     * @return
     */
    Mat readMatImg() {
        return imgSrc.getNextMat();
    }

    /**
     * Reads an image from video source and crops it.
     * The matrix is then converted to a JavaFx image
     * @return Image
     */
    public Image readImg() {
        Mat cropMat = this.cropMat(imgSrc.getNextMat());
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", cropMat, buffer);
        Image img = new Image(new ByteArrayInputStream(buffer.toArray()));
        return img;
    }

    /**
     * Matrix is cropped from all four sides.
     * @param oldMat Matrix that should be cropped
     * @return cropped Matrix
     */
    private Mat cropMat(Mat oldMat) {
        int rowStart = 0 + this.topCrop;
        int rowEnd = oldMat.rows() - this.bottomCrop;
        int colStart = 0 + this.leftCrop;
        int colEnd = oldMat.cols() - this.rightCrop;

        return oldMat.submat(rowStart, rowEnd, colStart, colEnd);
    }

    /**
     * Sets the type of image source depending on parameter x and prepares the connection.
     * 0: LivestreamSource
     * 1: OpenIGTLinkSource
     * 2: FilestreamSource
     * @param x int
     */
    public boolean openConnection(int x){
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
            return imgSrc.openConnection();
        } else {
            logger.log(Level.WARNING, "Image source could not be set.");
            return false;
        }
    }

    public boolean closeConnection(){
        return imgSrc.closeConnection();
    }

}

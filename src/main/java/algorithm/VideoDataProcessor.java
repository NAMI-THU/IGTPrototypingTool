package algorithm;

import inputOutput.*;
import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ImageDataProcessor accesses AbstractImageSource to control the connection and read images.
 *
 */
public class VideoDataProcessor {

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

    public static BufferedImage Mat2BufferedImage(Mat matrix){
        return (BufferedImage) HighGui.toBufferedImage(matrix);
    }
    public static Image Mat2Image(Mat matrix, String extension){
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(extension, matrix, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }
    /**
     * Old method to read image data. Uses Java.awt.BufferedImage
     * @return
     */
    BufferedImage readBufImg() {
        Mat mat = imgSrc.getNextMat();
        return Mat2BufferedImage(mat);
    }

    /**
     * Reads an image from video source and crops it.
     * The matrix is then converted to a JavaFx image
     * @return Image
     */
    public Image readImg() {
        Mat cropMat = this.cropMat(imgSrc.getNextMat());
        return Mat2Image(cropMat,".png");
    }

    public Mat readMat(){
        return imgSrc.getNextMat();
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

    public boolean openConnection(VideoSource source){
        return openConnection(source, 0);
    }
    /**
     * Sets the type of image source depending on parameters source and deviceId and prepares the connection.
     * @param source Input device
     * @param deviceId ID of the device (default: 0)
     */
    public boolean openConnection(VideoSource source, int deviceId){
        try {
            switch (source) {
                case LIVESTREAM:
                    imgSrc = LivestreamSource.forDevice(deviceId);
                    break;
                case OPENIGTLINK:
                    imgSrc = new OIGTImageSource();
                    break;
                case FILE:
                    imgSrc = new FilestreamSource(filePath);
                    break;
            }

            if (imgSrc != null) {
                return imgSrc.openConnection();
            } else {
                logger.log(Level.WARNING, "Image source could not be set.");
                return false;
            }
        }catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage());
            return false;
        }
    }

    public boolean closeConnection(){
        if(imgSrc != null) {
            return imgSrc.closeConnection();
        }
        return true;
    }

}

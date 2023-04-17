package algorithm;

import java.awt.image.BufferedImage;

import inputOutput.VideoSource;
import javafx.scene.image.Image;
import org.opencv.core.Mat;

/**
 * builds an interface to other teams, includes distance measurement and
 * conversion from matrix in bufferedimage
 * @author Team2
 *
 * Access to calculations with DistanceMeasurement.
 * Access to imagedata and its source/connection over ImageDataProcessor.
 */
public class ImageDataManager {

    ImageDataProcessor dataProcessor = new ImageDataProcessor();

    public boolean openConnection(VideoSource source){
        return dataProcessor.openConnection(source);
    }
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

    public ImageDataProcessor getDataProcessor() {
        return this.dataProcessor;
    }
}

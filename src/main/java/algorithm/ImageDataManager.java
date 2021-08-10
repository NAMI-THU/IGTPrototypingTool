package algorithm;

import java.awt.image.BufferedImage;

import javafx.scene.image.Image;

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

    public boolean openConnection (int x){
        return dataProcessor.openConnection(x);
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

    public ImageDataProcessor getDataProcessor() {
        return this.dataProcessor;
    }
}

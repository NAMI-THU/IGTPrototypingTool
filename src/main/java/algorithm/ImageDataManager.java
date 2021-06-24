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

/*
 * this was used for calculations with image data. delete maybe
 *
    DistanceMeasurement c = new DistanceMeasurement();

    public int getDistanceBox() {
        return c.getDistanceBox();
    }

    /**
     * get distance and convert in pixel in mm
     * @param x1 x-coordinate first point
     * @param y1 y-coordinate first point
     * @param x2 x-coordinate second point
     * @param y2 y-coordinate second point
     * @param umrechnungX
     * @param umrechnungY
     * @return Ergebnis in mm
     *
    public int getDistanceXY(int x1, int y1, int x2, int y2, double umrechnungX, double umrechnungY) {
        return c.getDistanceXY(x1, y1, x2, y2, umrechnungX, umrechnungY);
    }

    public int getDistancePoint(Point a, Point b) {
        return c.getDistancePoint(a, b);
    }

    public void MouseListenerPressed(MouseEvent evt) {
        c.MouseListenerPressed(evt);
    }

    public void MouseListenerReleased(MouseEvent evt) {
        c.MouseListenerReleased(evt);
    }
*/
    public void openConnection (int x){
        dataProcessor.openConnection(x);
    }

    public void closeConnection(){
        dataProcessor.closeConnection();
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

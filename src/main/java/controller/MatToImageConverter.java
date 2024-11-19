package controller;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MatToImageConverter {

    public static Image matToImage(Mat mat) {
        // Convert the Mat to an Image
        Mat convertedMat = new Mat();
        if (mat.channels() == 1) {
            Imgproc.cvtColor(mat, convertedMat, Imgproc.COLOR_GRAY2RGB);
        } else if (mat.channels() == 3) {
            Imgproc.cvtColor(mat, convertedMat, Imgproc.COLOR_BGR2RGB);
        }

        int width = convertedMat.cols();
        int height = convertedMat.rows();
        WritableImage image = new WritableImage(width, height);
        PixelWriter pixelWriter = image.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double[] pixel = convertedMat.get(y, x);
                Color color = Color.rgb((int) pixel[2], (int) pixel[1], (int) pixel[0]);
                pixelWriter.setColor(x, y, color);
            }
        }
        return image;
    }
}


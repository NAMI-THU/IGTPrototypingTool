package inputOutput;


import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;


/**
 * saves the imported frames as Mat Objects (OpenCV). For the saving process the VideoWriter class from the OpenCV library is used.
 * @author team3
 *
 */
public class ImageWriter {

    private VideoWriter writer;
    public String path;
    private Size size;
    private int fps;

    /**
     * This method needs a filename, frames-per-second and width & height of the matrix for creating an object of VideoWriter.
     * @param path
     * @param fps
     * @param width
     * @param height
     */
    public void saveVideo(String path, int fps, int width, int height) {

        this.path = path;
        this.fps = fps;
        size = new Size(width, height);

        int fourcc = VideoWriter.fourcc('D','V','I','X');
	    writer = new VideoWriter(path + ".mpg", fourcc, 30, size);
    }

    /**
     * This method is for saving the video by storing each Mat object that is received.
     * It only works if saveVideo was called before for setting the parameters.
     * @param frameMatrix
     */
    public void writeMat(Mat frameMatrix) {
        writer.write(frameMatrix);
    }


    /**
     * This method is for saving one single Image given as a parameter on the computer. A destination path is also necessary here as a parameter.
     * @param bufImg
     * @param path
     */
    public void saveImage(BufferedImage bufImg, String path) {

        File output = new File(path + ".jpg");
        try {
            ImageIO.write(bufImg,"jpg",output);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

}

package algorithm;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;

public class EnhanceImage {
    public static List<Mat> test = new ArrayList<Mat>();
    public static Image SharpenImage(Mat src, double sigmaColor, double sigmaSpace){        
       
        Mat graySrc = new Mat();
        Imgproc.cvtColor(src, graySrc, Imgproc.COLOR_BGR2GRAY);

        Mat resizedSrc = new Mat();
        Size scaleSize = new Size(960,540);
        Imgproc.resize(graySrc, resizedSrc, scaleSize);   
        Mat dst = new Mat(scaleSize, resizedSrc.type());
       
        //Photo.fastNlMeansDenoising(resizedSrc,dst, 5);//, 3, 9);
        //Imgproc.GaussianBlur(resizedSrc, dst, new Size(7, 7), 0);
        //Imgproc.medianBlur(resizedSrc, dst, 7);  
        // if(test.isEmpty()){
        //     test.add(resizedSrc);
        // }
        // Photo.denoise_TVL1(test, dst);
        //Imgproc.blur(resizedSrc, dst, new Size(5,5));
        if( sigmaColor > 0 || sigmaSpace > 0){
            Imgproc.bilateralFilter(resizedSrc, dst, 7, sigmaColor, sigmaSpace);
        }else{
            dst = resizedSrc;
        } 

        //convolution matrix to sharpen image
        Mat kernel = new Mat(3,3, CvType.CV_32F);
        kernel.put(0, 0, 0); kernel.put(0, 1, -1); kernel.put(0, 2, 0);
        kernel.put(1, 0, -1);kernel.put(1, 1,  5); kernel.put(1, 2, -1);
        kernel.put(2, 0, 0); kernel.put(2, 1, -1); kernel.put(2, 2, 0);

        Mat newResult = new Mat();
        Imgproc.filter2D(dst, newResult, -1, kernel);
        MatOfByte buffer2 = new MatOfByte();
        Imgcodecs.imencode(".png", newResult, buffer2);       
        return new Image(new ByteArrayInputStream(buffer2.toArray()));       

    }

}

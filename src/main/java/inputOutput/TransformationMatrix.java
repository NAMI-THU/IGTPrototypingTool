package inputOutput;

import com.google.gson.Gson;
import controller.AutoTrackController;
import org.opencv.core.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TransformationMatrix {
    public float[][] rotation3d = new float[][]{{1.0f,0.0f,0.0f},
            {0.0f,1.0f,0.0f},
            {0.0f, 0.0f, 1.0f}};
    public float[] translationVector3d = new float[]{0, 0, 0};
    public float[] scaleVector3d = new float[]{1, 1, 1};
    public int[][] flip2d = new int[][]{{1, 0},{0, 1}};

    public float[][] imagePoints;
    public float[][] trackingPoints;

    public int ignoreDimension = 2;

    public static TransformationMatrix loadFromJSON(String file) throws FileNotFoundException {
        return new Gson().fromJson(new FileReader(file), TransformationMatrix.class);
    }

    public void saveToJSON(File file) throws IOException {
        try (FileWriter fw = new FileWriter(file)) {
            new Gson().toJson(this, fw);
        }
    }

    public MatOfPoint2f getImagePoints(){
        if(imagePoints == null){return new MatOfPoint2f();}
        var array = new Point[4];
        for(int i = 0;i<imagePoints.length;i++){
            array[i] = new Point(imagePoints[i][0], imagePoints[i][1]);
        }
        return new MatOfPoint2f(array);
    }

    public MatOfPoint2f getTrackingPoints(){
        if(trackingPoints == null){return new MatOfPoint2f();}
        var array = new Point[4];
        for(int i = 0;i<trackingPoints.length;i++){
            array[i] = new Point(trackingPoints[i][0]+ AutoTrackController.TRACKING_SHIFT, trackingPoints[i][1]+ AutoTrackController.TRACKING_SHIFT);
        }
        return new MatOfPoint2f(array);
    }

    public Mat getRotationMat(){
        var mat = Mat.eye(2,3, CvType.CV_64F);
        int row = 0;
        for(int i = 0; i < rotation3d.length; i++){
            int col = 0;
            if(i == ignoreDimension){
                continue;
            }
            for(int j = 0; j < rotation3d[i].length; j++){
                if(j == ignoreDimension){
                    continue;
                }
                mat.put(row, col, rotation3d[i][j]);
                col++;
            }
            row++;
        }
        return mat;
    }

    public Mat getTranslationMat(){
        var mat = Mat.eye(2, 3, CvType.CV_64F);
        int row = 0;
        for(int i = 0;i < translationVector3d.length; i++){
            if(i == ignoreDimension){
                continue;
            }
            mat.put(row,2,translationVector3d[i]);
            row++;
        }
        return mat;
    }

    public Mat getScaleMat(){
        var mat = new Mat(2,3, CvType.CV_64F);
        for(int i = 0;i<mat.rows();i++){
            for(int j = 0;j<mat.cols();j++){
                float value = 0f;
                if(i == j){
                    value = scaleVector3d[i]*flip2d[i][j];
                }
                mat.put(i,j,value);
            }
        }
        return mat;
    }

    public Mat getTransformMat(){
        if(trackingPoints == null){
            return Mat.eye(3,3,CvType.CV_32F);
        }
        Mat track_m1 = toMat(trackingPoints);
        Mat track_m2 = toMat2(trackingPoints);
        var tmp2 = new Mat(3,1,CvType.CV_32F);
        Core.solve(track_m1, track_m2, tmp2);
        var A = scale(track_m1, tmp2);

        Mat img_m1 = toMat(imagePoints);
        Mat img_m2 = toMat2(imagePoints);
        var tmp1 = new Mat(3,1,CvType.CV_32F);
        Core.solve(img_m1, img_m2, tmp1);
        var B = scale(img_m1, tmp1);

        var C = B.mul(A.inv());
        return C;
    }

    private Mat scale(Mat m, Mat parameters){
        var lambda = parameters.get(0,0)[0];
        var mu = parameters.get(1,0)[0];
        var psi = parameters.get(2,0)[0];

        Mat out = new Mat(m.size(), CvType.CV_32F);
        out.put(0,0,m.get(0,0)[0]*lambda);
        out.put(1,0,m.get(1,0)[0]*lambda);
        out.put(2,0,lambda);

        out.put(0,1,m.get(0,1)[0]*mu);
        out.put(1,1,m.get(1,1)[0]*mu);
        out.put(2,1,mu);

        out.put(0,2,m.get(0,2)[0]*psi);
        out.put(1,2,m.get(1,2)[0]*psi);
        out.put(2,2,psi);
        return out;
    }
    private Mat toMat(float[][] points){
        Mat m1 = new Mat(3,3, CvType.CV_32F);
        m1.put(0,0,points[0][0]);
        m1.put(1,0,points[0][1]);
        m1.put(2,0,1);
        m1.put(0,1,points[1][0]);
        m1.put(1,1,points[1][1]);
        m1.put(2,1,1);
        m1.put(0,2,points[2][0]);
        m1.put(1,2,points[2][1]);
        m1.put(2,2,1);
        return m1;
    }

    private Mat toMat2(float[][] points){
        Mat m2 = new Mat(3,1, CvType.CV_32F);
        m2.put(0,0,imagePoints[3][0]);
        m2.put(1,0,imagePoints[3][1]);
        m2.put(2,0,1);
        return m2;
    }
}

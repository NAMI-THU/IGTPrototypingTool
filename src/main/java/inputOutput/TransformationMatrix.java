package inputOutput;

import algorithm.ProjectiveTransformation;
import com.google.gson.Gson;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;

import java.io.*;

public class TransformationMatrix {
    /**
     * The rotation matrix, provided by MITK
     */
    public float[][] rotation3d = new float[][]{{1.0f,0.0f,0.0f},
            {0.0f,1.0f,0.0f},
            {0.0f, 0.0f, 1.0f}};

    /**
     * The translation vector as provided by MITK
     */
    public float[] translationVector3d = new float[]{0, 0, 0};

    /**
     * Scaling vector (applied in the respective dimensions)
     */
    public float[] scaleVector3d = new float[]{1, 1, 1};

    /**
     * Factors for flipping (mirroring) the image
     */
    public int[][] flip2d = new int[][]{{1, 0},{0, 1}};

    /**
     * The raw measurements of the image points (landmarks in image coordinates)
     */
    public float[][] imagePoints;

    /**
     * The raw measurements of the tracker (landmarks in tracking coordinates)
     */
    public float[][] trackingPoints;

    /**
     * Assuming that rotation, translation and scale matrix uses 3 dimension, we skip this dimension in order to use it for 2D
     */
    public int ignoreDimension = 2;

    /**
     * Loads the attributes from a file
     * @param file Path of the json file
     * @return A TransformationMatrix-Object
     * @throws FileNotFoundException If the path is invalid
     */
    public static TransformationMatrix loadFromJSON(String file) throws FileNotFoundException {
        return new Gson().fromJson(new FileReader(file), TransformationMatrix.class);
    }

    /**
     * Writes the current configuration into a JSON file
     * @param file The file to write to
     * @throws IOException If problems occur
     */
    public void saveToJSON(File file) throws IOException {
        try (FileWriter fw = new FileWriter(file)) {
            new Gson().toJson(this, fw);
        }
    }

    /**
     * Provides the image points as OpenCV-Matrix
     * Only uses the first two coordinates of the point (2D)
     * @return the points as MatOfPoint2f
     */
    public MatOfPoint2f getImagePoints2d(){
        if(imagePoints == null){return new MatOfPoint2f();}
        var array = new Point[imagePoints.length];
        for(int i = 0;i<imagePoints.length;i++){
            array[i] = new Point(imagePoints[i][0], imagePoints[i][1]);
        }
        return new MatOfPoint2f(array);
    }

    /**
     * Provides the tracking points as OpenCV-Matrix
     * Only uses the first two coordinates of the point (2D)
     * @return the points as MatOfPoints2f
     */
    public MatOfPoint2f getTrackingPoints2d(){
        if(trackingPoints == null){return new MatOfPoint2f();}
        var array = new Point[trackingPoints.length];
        for(int i = 0;i<trackingPoints.length;i++){
            array[i] = new Point(trackingPoints[i][0], trackingPoints[i][1]);
        }
        return new MatOfPoint2f(array);
    }

    /**
     * Provides the image points as OpenCV-Matrix
     * Uses three point coordinates, to calculate in 3D
     * @return the points as MatOfPoint3f
     */
    public MatOfPoint3f getImagePoints3d(){
        if(imagePoints == null){return new MatOfPoint3f();}
        var array = new Point3[imagePoints.length];
        for(int i = 0;i<imagePoints.length;i++){
            array[i] = new Point3(imagePoints[i][0], imagePoints[i][1], imagePoints[i][2]);
        }
        return new MatOfPoint3f(array);
    }

    /**
     * Provides the tracking points as OpenCV-Matrix
     * Uses three point coordinates, to calculate in 3D
     * @return the points as MatOfPoints3f
     */
    public MatOfPoint3f getTrackingPoints3d(){
        if(trackingPoints == null){return new MatOfPoint3f();}
        var array = new Point3[trackingPoints.length];
        for(int i = 0;i<trackingPoints.length;i++){
            array[i] = new Point3(trackingPoints[i][0], trackingPoints[i][1], trackingPoints[i][2]);
        }
        return new MatOfPoint3f(array);
    }

    /**
     * Converts the rotation matrix array into an OpenCV-Matrix
     * @return The converted matrix
     */
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

    /**
     * Converts the translation vector array into an OpenCV-Matrix
     * @return The converted matrix
     */
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

    /**
     * Converts the scale vector array into an OpenCV-Matrix
     * @return The converted matrix
     */
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

    /**
     * Calculates a transformation matrix by the provided raw points.
     * Uses the method of finding the projective transform.
     * See also https://math.stackexchange.com/a/339033
     * !! Only works with a 2D-representation of the points. !!
     * @return The resulting transformation matrix
     */
    public Mat getTransformMatProjectionTransform(){
        return ProjectiveTransformation.getTransformMatProjectionTransform(imagePoints, trackingPoints);
    }

    /**
     * Calculates a transformation matrix by the provided raw points
     * Uses OpenCV's estimate-method based on RANSAC method
     * Uses only the x,y coordinates of the points and hence only calculates a 2D projection matrix
     * @return The transformation matrix
     */
    public Mat getTransformMatOpenCvEstimated2d(){
        if(trackingPoints == null || imagePoints == null){
            return Mat.eye(2,3,CvType.CV_64F);
        }
        var imagePoints = getImagePoints2d();
        var trackingPoints = getTrackingPoints2d();
        return Calib3d.estimateAffine2D(trackingPoints,imagePoints);
    }

    /**
     * Calculates a transformation matrix by the provided raw points
     * Uses OpenCV's estimate-method based on RANSAC method
     * Uses x,y,z coordinates of the points and hence calculates a 3D projection matrix
     * @return The transformation matrix
     */
    public Mat getTransformMatOpenCvEstimated3d(){
        if(trackingPoints == null || imagePoints == null){
            return Mat.eye(3,4,CvType.CV_64F);
        }
        var imagePoints = getImagePoints3d();
        var trackingPoints = getTrackingPoints3d();
        var outputMat = new Mat();
        var inlierMat = new Mat();
        Calib3d.estimateAffine3D(trackingPoints,imagePoints, outputMat, inlierMat);
        return outputMat;
    }
}

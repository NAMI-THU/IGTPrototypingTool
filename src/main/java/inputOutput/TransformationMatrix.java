package inputOutput;

import com.google.gson.Gson;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.*;

public class TransformationMatrix {
    public float[][] matrix3d = new float[][]{{1.0f,0.0f,0.0f},
                                                {0.0f,1.0f,0.0f},
                                                {0.0f, 0.0f, 1.0f}};
    public float[] translationVector3d = new float[]{0, 0, 0};
    public float[] scaleVector3d = new float[]{1, 1, 1};

    public int ignoreDimension = 2;

    public static TransformationMatrix loadFromJSON(String file) throws FileNotFoundException {
        return new Gson().fromJson(new FileReader(file), TransformationMatrix.class);
    }

    public void saveToJSON(File file) throws IOException {
        try (FileWriter fw = new FileWriter(file)) {
            new Gson().toJson(this, fw);
        }
    }

    public Mat getRotationMat(){
        var mat = new Mat(2,2, CvType.CV_64F);
        int row = 0;
        for(int i = 0; i < matrix3d.length; i++){
            int col = 0;
            if(i == ignoreDimension){
                continue;
            }
            for(int j = 0; j < matrix3d[i].length; j++){
                if(j == ignoreDimension){
                    continue;
                }
                mat.put(row, col, matrix3d[i][j]);
                col++;
            }
            row++;
        }
        return mat;
    }

    public Mat getTranslationMat(){
        var mat = new Mat(1, 2, CvType.CV_64F);
        int col = 0;
        for(int i = 0;i < translationVector3d.length; i++){
            if(i == ignoreDimension){
                continue;
            }
            mat.put(0,col,translationVector3d[i]);
            col++;
        }
        return mat;
    }

    public Mat getScaleMat(){
        var mat = new Mat(1, 2, CvType.CV_64F);
        int col = 0;
        for(int i = 0;i< scaleVector3d.length;i++){
            if(i == ignoreDimension){
                continue;
            }
            mat.put(0, col, scaleVector3d[i]);
            col++;
        }
        return mat;
    }

    public Mat getOverallTransformationMat(){
        var rotation = getRotationMat();
        var translation = getTranslationMat();
        var scale = getScaleMat();

        var mat = new Mat(2, 3, CvType.CV_32F);
        mat.put(0,0,rotation.get(0,0)[0] * scale.get(0,0)[0]);
        mat.put(0,1,rotation.get(0,1)[0]);
        mat.put(0,2,translation.get(0,0)[0]);

        mat.put(1,0,rotation.get(1,0)[0]);
        mat.put(1,1,rotation.get(1,1)[0] * scale.get(0,1)[0]);
        mat.put(1,2,translation.get(0,1)[0]);

        return mat;
    }
}

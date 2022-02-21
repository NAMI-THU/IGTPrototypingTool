package inputOutput;

import com.google.gson.Gson;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.*;

public class TransformationMatrix {
    public double[][] matrix = new double[][]{  {1,0,0},
                                                {0,1,0}};

    public static TransformationMatrix loadFromJSON(String file) throws FileNotFoundException {
        return new Gson().fromJson(new FileReader(file), TransformationMatrix.class);
    }

    public void saveToJSON(File file) throws IOException {
        try (FileWriter fw = new FileWriter(file)) {
            new Gson().toJson(this, fw);
        }
    }

    public Mat toMat(){
        var mat = new Mat(matrix.length, matrix[0].length, CvType.CV_32F);
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                mat.put(i, j, matrix[i][j]);
            }
        }
        return mat;
    }
}

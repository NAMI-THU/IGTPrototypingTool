package algorithm;

import org.opencv.core.Mat;

public class MatHelper {
    public static double[][] matToArray(Mat mat){
        double[][] outArray = new double[mat.rows()][];
        for(int r = 0;r < mat.rows(); r++){
            outArray[r] = new double[mat.cols()];
            for(int c = 0;c<mat.cols();c++){
                outArray[r][c] = mat.get(r,c)[0];
            }
        }
        return outArray;
    }

    public static boolean isEmpty(Mat mat){
        for(int r = 0;r<mat.rows();r++){
            for(int c=0;c<mat.cols();c++){
                if(mat.get(r,c)[0] != 0){
                    return false;
                }
            }
        }
        return true;
    }

    public static int[] calculateRoi(Mat mat){
        int beginRow = 0;
        int endRow = mat.rows()-1;
        int beginCol = 0;
        int endCol = mat.cols()-1;
        for(int i = 0;i<mat.rows()-1;i++){
            if(MatHelper.isEmpty(mat.row(i)) && !MatHelper.isEmpty(mat.row(i+1))){
                beginRow = i;
            }else if(!MatHelper.isEmpty(mat.row(i)) && MatHelper.isEmpty(mat.row(i+1))){
                endRow = i+1;
            }
        }
        for(int i = 0;i<mat.cols()-1;i++){
            if(MatHelper.isEmpty(mat.col(i)) && !MatHelper.isEmpty(mat.col(i+1))){
                beginCol = i;
            }else if(!MatHelper.isEmpty(mat.col(i)) && MatHelper.isEmpty(mat.col(i+1))){
                endCol = i+1;
            }
        }
        return new int[]{beginRow, endRow, beginCol, endCol};
    }
}

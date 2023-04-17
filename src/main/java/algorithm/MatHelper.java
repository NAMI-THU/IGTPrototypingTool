package algorithm;

import org.opencv.core.Mat;

public class MatHelper {

    /**
     * Converts an OpenCV-Mat to a double array
     * @param mat Matrix to convert
     * @return The matrix as array
     */
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

    /**
     * Returns whether the provided Row/Column is empty.
     * @param mat The row-/column-vector
     * @return true if the vector contains only zeros, false if not
     */
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

    /**
     * Calculates the region of interest. This is the region of the image that is not empty.
     * @param mat Input image
     * @param noExecute Whether the true region shall be calculated. If set to false, the image won't be changed (for testing purpose)
     * @return Indices of the submat with content
     */
    public static int[] calculateRoi(Mat mat, boolean noExecute){
        int beginRow = 0;
        int endRow = mat.rows()-1;
        int beginCol = 0;
        int endCol = mat.cols()-1;

        if(!noExecute) {
            for (int i = 0; i < mat.rows() - 1; i++) {
                if (MatHelper.isEmpty(mat.row(i)) && !MatHelper.isEmpty(mat.row(i + 1))) {
                    beginRow = i;
                } else if (!MatHelper.isEmpty(mat.row(i)) && MatHelper.isEmpty(mat.row(i + 1))) {
                    endRow = i + 1;
                }
            }
            for (int i = 0; i < mat.cols() - 1; i++) {
                if (MatHelper.isEmpty(mat.col(i)) && !MatHelper.isEmpty(mat.col(i + 1))) {
                    beginCol = i;
                } else if (!MatHelper.isEmpty(mat.col(i)) && MatHelper.isEmpty(mat.col(i + 1))) {
                    endCol = i + 1;
                }
            }
        }
        return new int[]{beginRow, endRow, beginCol, endCol};
    }
}

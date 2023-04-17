package algorithm;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class ProjectiveTransformation {
    /**
     * Calculates a transformation matrix by the provided raw points.
     * Uses the method of finding the projective transform.
     * See also https://math.stackexchange.com/a/339033
     * @return The resulting transformation matrix
     */
    public static Mat getTransformMatProjectionTransform(float[][] imagePoints, float[][] trackingPoints){
        if(trackingPoints == null || imagePoints == null){
            return Mat.eye(3,3, CvType.CV_64F);
        }
        Mat track_m1 = getLeftSideMat(trackingPoints);
        Mat track_m2 = getRightSideMat(trackingPoints);
        var tmp2 = new Mat(3,1,CvType.CV_64F);
        Core.solve(track_m1, track_m2, tmp2);
        var A = scaleByVariables(track_m1, tmp2);

        Mat img_m1 = getLeftSideMat(imagePoints);
        Mat img_m2 = getRightSideMat(imagePoints);
        var tmp1 = new Mat(3,1,CvType.CV_64F);
        Core.solve(img_m1, img_m2, tmp1);
        var B = scaleByVariables(img_m1, tmp1);

        //var C = B.mul(A.inv());
        var C = new Mat();
        Core.gemm(B, A.inv(),1, new Mat(),1,C);
        return C;
    }

    /**
     * Provides a matrix of the first 3 points for the left-hand side of the equation
     * @param points All measured points (either image- or tracking-based)
     * @return The points as matrix
     */
    private static Mat getLeftSideMat(float[][] points){
        Mat m1 = new Mat(3,3, CvType.CV_64F);
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

    /**
     * Provides a matrix of the last point for the right-hand side of the equation
     * @param points All measured points (either image- or tracking-based)
     * @return The point as matrix
     */
    private static Mat getRightSideMat(float[][] points){
        Mat m2 = new Mat(3,1, CvType.CV_64F);
        m2.put(0,0,points[3][0]);
        m2.put(1,0,points[3][1]);
        m2.put(2,0,1);
        return m2;
    }

    /**
     * Scales the matrix by the 3 variables lambda, mu and tau
     * @param m The matrix to scale
     * @param parameters The three parameters lambda, mu and tau
     * @return The scaled matrix
     */
    private static Mat scaleByVariables(Mat m, Mat parameters){
        var lambda = parameters.get(0,0)[0];
        var mu = parameters.get(1,0)[0];
        var tau = parameters.get(2,0)[0];

        Mat out = new Mat(m.size(), CvType.CV_64F);
        out.put(0,0,m.get(0,0)[0]*lambda);
        out.put(1,0,m.get(1,0)[0]*lambda);
        out.put(2,0,lambda);

        out.put(0,1,m.get(0,1)[0]*mu);
        out.put(1,1,m.get(1,1)[0]*mu);
        out.put(2,1,mu);

        out.put(0,2,m.get(0,2)[0]*tau);
        out.put(1,2,m.get(1,2)[0]*tau);
        out.put(2,2,tau);
        return out;
    }
}

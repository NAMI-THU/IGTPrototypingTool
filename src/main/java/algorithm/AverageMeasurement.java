package algorithm;

import com.jme3.math.Quaternion;

import java.util.ArrayList;
import java.util.List;

public class AverageMeasurement extends Measurement {

    Quaternion RotationJitter;
    private Quaternion rotationError;
    private List<Double> errors = new ArrayList<>();
    private BoxPlot boxPlot;

    public AverageMeasurement() {
        super();
    }

    public Quaternion getRotationJitter() {
        return RotationJitter;
    }

    public void setRotationJitter(Quaternion rotationJitter) {
        RotationJitter = rotationJitter;
    }

    public BoxPlot getBoxPlot() {
        return boxPlot;
    }

    public void setBoxPlot(BoxPlot boxPlot) {
        this.boxPlot = boxPlot;
    }

    public List<Double> getErrors() {
        return errors;
    }

    public void setErrors(List<Double> errors) {
        this.errors = errors;
    }

    public Quaternion getRotationError() {
        return rotationError;
    }

    public void setRotationError(Quaternion rotationError) {
        this.rotationError = rotationError;
    }

    /**
     * This method computes the root mean square error. She receives a list of
     * errors. In a loop the errors are added and taken in square. The RMSE is
     * calculated with the square of additionalPowError divided by the number of
     * errors.
     *
     * @return rmse - the Root Mean Square Error, which is just the square root of
     * the mean square error
     */
    private double getRMSE() {
        double additionalPowError = 0;

        for (double error : errors) {
            additionalPowError += Math.pow(error, 2);
        }
        double rmse = Math.sqrt(additionalPowError / errors.size());
        return rmse;
    }

    /**
     * The method getJitter computes the root mean square error. She receives a list
     * of errors and called method getRMSE, where the RMSE is calculated.
     *
     * @return getRMSE - the root mean square error
     */
    public double getJitter() {
        return getRMSE();
    }

}

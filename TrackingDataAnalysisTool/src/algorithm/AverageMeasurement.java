package algorithm;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Quaternion;

public class AverageMeasurement extends Measurement {

	public AverageMeasurement() {
		super();
	}

	private double error;
	private Quaternion rotationError;
	private List<Double> errors = new ArrayList<>();
	private BoxPlot boxPlot;

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

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
	}

	public Quaternion getRotationError() {
		return rotationError;
	}

	public void setRotationError(Quaternion rotationError) {
		this.rotationError = rotationError;
	}

}

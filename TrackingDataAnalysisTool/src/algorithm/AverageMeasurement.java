package algorithm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.complex.Quaternion;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;

import javafx.geometry.Point3D;

public class AverageMeasurement extends Measurement{

	AverageMeasurement(){
		super();
	}
	
	private double error;
	private RotationError rotationError;
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

	public RotationError getRotationError() {
		return rotationError;
	}

	public void setRotationError(RotationError rotationError) {
		this.rotationError = rotationError;
	}
	
	

}

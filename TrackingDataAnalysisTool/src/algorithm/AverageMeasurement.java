package algorithm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.complex.Quaternion;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;

import javafx.geometry.Point3D;

public class AverageMeasurement extends Measurement{

//	AverageMeasurement(){
//		super();
//	}
	
	private double error;
	private double rotationError;
	
	public double getError() {
		return error;
	}
	
	public void setError(double error) {
		this.error = error;
	}

	public double getRotationError() {
		return rotationError;
	}

	public void setRotationError(double rotationError) {
		this.rotationError = rotationError;
	}
	
	

}

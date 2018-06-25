package algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javafx.geometry.Point3D;

//import org.apache.commons.math3.complex.Quaternion;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import com.jme3.math.Quaternion;

public class DataProcessor {

	public double getAccuracy(double expectedDistance, AverageMeasurement firstAverangeMeasurement,
			AverageMeasurement secondAverangeMeasurement) {
		return getDistance(firstAverangeMeasurement.getPoint(), secondAverangeMeasurement.getPoint())
				- expectedDistance;
	}

	public double getAccuracyRotation(Quaternion expectedRotation, Measurement firstMeasurement,
			Measurement secondMeasurement) {
		return getRotationIndize(firstMeasurement.getRotation().subtract(secondMeasurement.getRotation()).subtract(expectedRotation));
	}

	public BoxPlot getBoxPlot(List<Double> values) {

		BoxPlot boxPlot = new BoxPlot();
		Collections.sort(values);
		Percentile percentile = new Percentile();
		percentile.setData(toDoubleArray(values));

		boxPlot.setMax(Collections.max(values));
		boxPlot.setMin(Collections.min(values));
		boxPlot.setQ1(percentile.evaluate(25));
		boxPlot.setMedian(percentile.evaluate(50));
		boxPlot.setQ3(percentile.evaluate(75));

		return boxPlot;
	}

	double[] toDoubleArray(List<Double> list) {
		double[] ret = new double[list.size()];
		int i = 0;
		for (Double e : list)
			ret[i++] = e.intValue();
		return ret;
	} 

	// Methode zum Berechnen des Mittelwerts;
	public AverageMeasurement getAverageMeasurement(List<Measurement> measurements) {

		int measureSize = measurements.size();
		Point3D addPoint = new Point3D(0, 0, 0);

		for (int i = 0; i < measureSize; i++) {
			Measurement measurement = measurements.get(i);
			Point3D point = measurement.getPoint();
			addPoint = addPoint.add(point);
		}

		double averageX = addPoint.getX() / measureSize;
		double averageY = addPoint.getY() / measureSize;
		double averageZ = addPoint.getZ() / measureSize;

		AverageMeasurement averageMeasurement = new AverageMeasurement();
		Point3D averagePoint = new Point3D(averageX, averageY, averageZ);

		averageMeasurement.setPoint(averagePoint);
		averageMeasurement.setRotation(getAverageRotation(measurements));
		return averageMeasurement;
	}
	
	public Quaternion getAverageRotation(List<Measurement> measurements) {
		
		Quaternion firstRotation =  measurements.get(0).getRotation();
		
		Quaternion lastRotation = measurements.get(measurements.size() -1).getRotation();
		
		//Zeit durch Anzahl teilen 
		//(bei diesem Wert ist die Bewegung genau die des durchschnitts) 
		//(insofern das Tool auf dem küzesten weg nach lastRotation bewegt wurde = kein richtungswechel in der Bewegung)
		float positionAtTime = 1/measurements.size(); 
		
		return firstRotation.slerp(firstRotation, lastRotation, positionAtTime);
	}

	public List<Double> getErrors(List<Measurement> measurements, Point3D avgPoint) {
		List<Double> errors = new ArrayList<>();

		for (int i = 0; i < measurements.size(); i++) {
			Point3D point = measurements.get(i).getPoint();
			double distance = getDistance(point, avgPoint);
			errors.add(distance);
		}
		return errors;
	}

	public double getJitter(List<Double> errors) {
		return getRMSE(errors);
	}

	public RotationError getRotationJitter(List<Measurement> measurements, Quaternion avgRotation) {

		List<Double> rotationPositionErrors = new ArrayList<>();
		RotationError rotationError = new RotationError();

		for (int i = 0; i < measurements.size(); i++) {
			
			Quaternion rotationMovement = measurements.get(i).getRotation();
			// vorraussetzung: liste muss nach zeitstempel sortiert sein
			if(i > 0) {
				rotationMovement = rotationMovement.subtract(measurements.get(i -1).getRotation());
			}
			
			Quaternion errorRotationofIterate = rotationMovement.subtract(avgRotation);
			//zusammenfassen der "einzelnen" Fehler
			double Indize = getRotationIndize(errorRotationofIterate);
			rotationPositionErrors.add(Indize);
		}

		rotationError.setRotationPositionError(getRMSE(rotationPositionErrors));

		return rotationError;
	}
	
	//geht nicht da Quaternion an position 0 ist w = 1 
	private double getRotationIndize(Quaternion rotation) {
		return rotation.getX() + rotation.getY() + rotation.getZ() + rotation.getW();
	}

	// root mean square error
	private double getRMSE(List<Double> errors) {
		double additionalPowError = 0;

		for (double error : errors) {
			additionalPowError += Math.pow(error, 2);
		}
		return Math.sqrt(additionalPowError / errors.size());
	}

	// calculate distance of two points
	private double getDistance(Point3D firstPoint, Point3D secondPoint) {
		return firstPoint.distance(secondPoint);
	}
}
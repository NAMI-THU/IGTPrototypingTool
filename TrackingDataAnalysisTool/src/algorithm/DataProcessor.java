package algorithm;

import java.util.ArrayList;
import java.util.List;

public class DataProcessor {

	public double getAccuracy(double expectedDistance, List<Point> firstPoints, List<Point> secondPoints) {
		double appendedDistance = 0;
		for (int i = 0; i < firstPoints.size(); i++) {
			appendedDistance += getDistance(firstPoints.get(i), secondPoints.get(i));
		}
		double avgDistance = appendedDistance / firstPoints.size();
		return avgDistance - expectedDistance;
	}

	public ToolMeasure getAverageMeasurement(ToolMeasure tool) {
		return tool;
	}

	public ToolMeasure getJitter(ToolMeasure tool) {

		List<Measurement> measurements = tool.getMeasurement(); // measurements of the tool
		Measurement averageMeasurement = tool.getAverageMeasurement(); // average measurement of the tool

		for (int f = 0; f < averageMeasurement.getPoints().size(); f++) {
			Point avgPoint = averageMeasurement.getPoints().get(f); // point of average measurement
			List<Double> errors = new ArrayList<>();
			for (int i = 0; i < measurements.size(); i++) {
				Measurement measurement = measurements.get(i);
				Point point = measurement.getPoints().get(f);

				double distance = getDistance(point, avgPoint);

				errors.add(distance);
			}
			avgPoint.setError(getRMSE(errors));
		}

		return tool;
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

	private double getDistance(Point firstPoint, Point secondPoint) {

		double dX = Math.pow(firstPoint.getX() - secondPoint.getX(), 2);
		double dY = Math.pow(firstPoint.getY() - secondPoint.getY(), 2);
		double dZ = Math.pow(firstPoint.getZ() - secondPoint.getZ(), 2);

		return Math.sqrt(dX + dY + dZ);

	}
}
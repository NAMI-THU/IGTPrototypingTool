package algorithm;

import java.util.ArrayList;
import java.util.List;

public class DataProcessor {

	
	//Constructor
	public DataProcessor() {}
	
	
	public void getAccuracy () {
	 
} 

//Methode zum Berechnen des Mittelwerts;

	public ToolMeasure getAverageMeasurement(ToolMeasure tool) {
		
		
		List<Measurement> measurements = tool.getMeasurement();
		
		Measurement averageMeasurement = new Measurement();
		
		
		for(int j=0; j < measurements.get(0).getPoints().size() ; j++) {
			
			double averageX = 0.0;
			double averageY = 0.0;
			double averageZ = 0.0;
			
			
			for(int i = 0; i < measurements.size(); i++) {
				
				Measurement measurement = measurements.get(i);
				Point point = measurement.getPoints().get(j);
				
				averageX += point.getX();
				averageY += point.getY();
				averageZ += point.getZ();
			}
			
			Point averagePoint = new Point((averageX / measurements.size()), (averageY / measurements.size()), (averageZ / measurements.size()));
			averageMeasurement.addPoint(averagePoint);
		}
		
		
		return tool;
		
		
		//System.out.println("Der Mittelwert beträgt: " + average);
	}

	public ToolMeasure getJitter(ToolMeasure tool) {

		List<Measurement> measurements = tool.getMeasurement(); // Messungen von dem Tool
		Measurement averageMeasurement = tool.getAverageMeasurement(); // durschnittliche Messung von dem Tool

		for (int f = 0; f < averageMeasurement.getPoints().size(); f++) {
			Point avgPoint = averageMeasurement.getPoints().get(f); // Punkte der durchschnittlichen Messung
			double appendedDistance = 0;
			for (int i = 0; i < measurements.size(); i++) {
				Measurement measurement = measurements.get(i);
				Point point = measurement.getPoints().get(f);

				double distance = getDistance(point, avgPoint);

				appendedDistance += distance;
				point.setJitter(distance);

				// Durchschnitt bei letzer Messung berechnen

				if (i == measurements.size() - 1) {
					double avgDistance = appendedDistance / measurements.size();
					avgPoint.setJitter(avgDistance);
				}

			}
		}

		return tool;
	}



	// Methode zum Berechnen der Distanz zweier Punkte


	private double getDistance(Point firstPoint, Point secondPoint) {

		double dX = Math.pow(firstPoint.getX() - secondPoint.getX(), 2);
		double dY = Math.pow(firstPoint.getY() - secondPoint.getY(), 2);
		double dZ = Math.pow(firstPoint.getZ() - secondPoint.getZ(), 2);

		return Math.sqrt(dX + dY + dZ);

	}
}
package algorithm;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import algorithm.DataProcessor;
import algorithm.Measurement;
import algorithm.Point;
import algorithm.ToolMeasure;
import javafx.geometry.Point3D;

public class AlgorithmTest2 {

	private DataProcessor processor;
	private ToolMeasure testTool;

	@Test
	public void getAverageMeasurementIsCorrect() {

		setUpData();

		ToolMeasure result = processor.getAverageMeasurement(testTool);

		Point3D expectedPoint = new Point3D(2,2,2);
		Point3D avgPoint = result.getAverageMeasurement().getPoint();
		assertTrue(avgPoint.getX() == expectedPoint.getX());
		assertTrue(avgPoint.getY() == expectedPoint.getY());
		assertTrue(avgPoint.getZ() == expectedPoint.getZ());

	}
	
	@Test
	public void getJitterIsCorrect() {
		setUpData();

		ToolMeasure result = processor.getJitter(testTool);

		assertTrue(result.getAverageMeasurement().getError() == 1.414213562373095);

	}

	private void setUpData() {

		processor = new DataProcessor();
		testTool = new ToolMeasure("TestTool");

		Measurement measurement1 = new Measurement();
		Measurement measurement2 = new Measurement();
		Measurement measurement3 = new Measurement();
		
		AverageMeasurement avgM = new AverageMeasurement();

		Point3D p1 = new Point3D(1, 1, 1);
		Point3D p2 = new Point3D(2, 2, 2);
		Point3D p3 = new Point3D(3, 3, 3);

		measurement1.setPoint(p1);
		measurement2.setPoint(p2);
		measurement3.setPoint(p3);
		
		avgM.setPoint(p2);

		testTool.addMeasurement(measurement1);
		testTool.addMeasurement(measurement2);
		testTool.addMeasurement(measurement3);
		
		testTool.setAverageMeasurement(avgM);

	}

}





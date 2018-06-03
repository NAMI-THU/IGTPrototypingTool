package testalgorithm;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.util.FastMath;
import org.junit.Test;

import algorithm.AverageMeasurement;
import algorithm.BoxPlot;
import algorithm.DataProcessor;
import algorithm.DataService;
import algorithm.Measurement;
import algorithm.RotationError;
import algorithm.ToolMeasure;
import javafx.geometry.Point3D;

public class AlgorithmTest {

	private DataProcessor processor;
	private DataService dataService;
	private ToolMeasure testTool;
	AverageMeasurement firstAverangeMeasurement;
	AverageMeasurement secondAverangeMeasurement;

	@Test
	public void getAverageMeasurementIsCorrect() {
		setUpData();

		AverageMeasurement result = processor.getAverageMeasurement(testTool.getMeasurement());

		Point3D expectedPoint = new Point3D(2, 2, 2);
		Point3D avgPoint = result.getPoint();

		assertTrue(avgPoint.getX() == expectedPoint.getX());
		assertTrue(avgPoint.getY() == expectedPoint.getY());
		assertTrue(avgPoint.getZ() == expectedPoint.getZ());

	}

	@Test
	public void getJitterIsCorrect() {
		setUpData();

		double result = processor.getJitter(testTool.getAverageMeasurement().getErrors());

		assertTrue(result == 1.414213562373095);

	}

	@Test
	public void getAccuracyIsCorrect() {

		setUpDataAccuracy();
		double expectedDistance = 1.7320508075688772;

		double result = processor.getAccuracy(expectedDistance, firstAverangeMeasurement, secondAverangeMeasurement);

		// keine Abweichung erwartet
		assertTrue(result == 0);
	}

	private void setUpDataAccuracy() {
		processor = new DataProcessor();

		firstAverangeMeasurement = new AverageMeasurement();
		secondAverangeMeasurement = new AverageMeasurement();

		Point3D p1 = new Point3D(1, 1, 1);
		Point3D p2 = new Point3D(2, 2, 2);
		Rotation r1 = new Rotation(0.1, 1, 1, 1, true);
		Rotation r2 = new Rotation(0.5, 2, 2, 2, true);

		firstAverangeMeasurement.setPoint(p1);
		secondAverangeMeasurement.setPoint(p2);
		firstAverangeMeasurement.setRotation(r1);
		secondAverangeMeasurement.setRotation(r2);
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
		Rotation r1 = new Rotation(0.1, 1, 1, 1, true);
		Rotation r2 = new Rotation(0.5, 2, 2, 2, true);
		Rotation r3 = new Rotation(1.0, 3, 3, 3, true);

		measurement1.setPoint(p1);
		measurement1.setRotation(r1);
		measurement2.setPoint(p2);
		measurement2.setRotation(r2);
		measurement3.setPoint(p3);
		measurement3.setRotation(r3);

		avgM.setPoint(p2);
		avgM.setRotation(r2);

		testTool.addMeasurement(measurement1);
		testTool.addMeasurement(measurement2);
		testTool.addMeasurement(measurement3);

		testTool.setAverageMeasurement(avgM);

	}

}

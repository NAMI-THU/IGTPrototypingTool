package testalgorithm;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.jme3.math.Quaternion;

import algorithm.AverageMeasurement;
import algorithm.BoxPlot;
import algorithm.DataProcessor;
import algorithm.DataService;
import algorithm.Measurement;
import algorithm.ToolMeasure;
import javafx.geometry.Point3D;

public class AlgorithmTest {

	private DataProcessor processor;
	private DataService dataService;
	private ToolMeasure testTool;
	AverageMeasurement firstAverangeMeasurement;
	AverageMeasurement secondAverangeMeasurement;
	Measurement firstMeasurement;
	Measurement secondMeasurement;

	@Test
	/**
	 * {@link DataProcessor}
	 * {@link DataProcessor#getAverageMeasurement(AverageMeasurement)}
	 */
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
	/**
	 * {@link DataProcessor} {@link DataProcessor#getJitter(double)}
	 */
	public void getJitterIsCorrect() {
		setUpData();

		double result = processor
				.getJitter(processor.getErrors(testTool.getMeasurement(), testTool.getAverageMeasurement().getPoint()));

		assertTrue(result == 1.414213562373095);

	}

	@Test
	/**
	 * {@link DataProcessor} {@link DataProcessor#getAccuracy(double)}
	 */
	public void getAccuracyIsCorrect() {

		setUpDataAccuracy();
		double expectedDistance = 1.7320508075688772;

		double result = processor.getAccuracy(expectedDistance, firstAverangeMeasurement, secondAverangeMeasurement);

		// no deviation expected
		assertTrue(result == 0);
	}

	@Test
	/**
	 * {@link DataProcessor} {@link DataProcessor#getRotationJitter(List)}
	 */
	public void getRotationJitterIsCorrect() {
		setUpData();

		Quaternion result = processor.getRotationJitter(testTool.getMeasurement(),
				new Quaternion((float) 0, (float) 0, (float) 0, (float) 1));

		assertTrue(result.equals(new Quaternion((float) 0, (float) 0, (float) 0, (float) 0.8164966)));

	}

	@Test
	/**
	 * {@link DataProcessor} {@link DataProcessor#getAccuracyRotation(Measurement)}
	 */
	public void getAccuracyRotationIsCorrect() {

		setUpDataAccuracy();
		Quaternion expectedRotation = new Quaternion((float) 0, (float) 0.7071, (float) 0, (float) 0.7071);

		Quaternion result = processor.getAccuracyRotation(expectedRotation, firstMeasurement, secondMeasurement);

		// no deviation expected
		assertTrue(result.equals(new Quaternion((float) 0, (float) 0, (float) 0, (float) -1)));
	}

	@Test
	/**
	 * {@link DataProcessor} {@link DataProcessor#getBoxPlot(double)}
	 */
	public void getBoxPlotIsCorrect() {

		processor = new DataProcessor();

		List<Double> values = new ArrayList<>();
		values.add(1.0);
		values.add(2.0);
		values.add(3.0);
		values.add(4.0);
		values.add(5.0);

		BoxPlot result = processor.getBoxPlot(values);

		// no deviation expected
		assertTrue(result.getMin() == 1);
		assertTrue(result.getQ1() == 1.5);
		assertTrue(result.getMedian() == 3);
		assertTrue(result.getQ3() == 4.5);
		assertTrue(result.getMax() == 5);

		System.out.println(
				"BoxPlot Werte: \n Min: " + result.getMin() + "\n" + " 1.Q : " + result.getQ1() + "\n" + " Median: "
						+ result.getMedian() + "\n" + " 3.Q: " + result.getQ3() + "\n" + " Max: " + result.getMax());

	}

	@Test
	/**
	 * {@link DataService} {@link DataService#getToolByName(String)}
	 */
	public void getToolByNameCorrect() {

		dataService = new DataService();
		setUpData();

		ToolMeasure result = null;

		try {
			result = dataService.getToolByName("TestTool");

		} catch (Exception e) {

			assertTrue(e.getMessage().equals("Tool not found: TestTool"));
		}

	}

	private void setUpDataAccuracy() {
		processor = new DataProcessor();

		firstAverangeMeasurement = new AverageMeasurement();
		secondAverangeMeasurement = new AverageMeasurement();

		firstMeasurement = new Measurement();
		secondMeasurement = new Measurement();

		Point3D p1 = new Point3D(1, 1, 1);
		Point3D p2 = new Point3D(2, 2, 2);
		Quaternion quaternion1 = new Quaternion((float) 0, (float) 0, (float) 0, (float) 1);
		Quaternion quaternion2 = new Quaternion((float) 0, (float) 0.7071, (float) 0, (float) 0.7071);
		firstMeasurement.setRotation(quaternion1);
		secondMeasurement.setRotation(quaternion2);

		firstAverangeMeasurement.setPoint(p1);
		secondAverangeMeasurement.setPoint(p2);
	}

	private void setUpData() {

		processor = new DataProcessor();
		testTool = new ToolMeasure("TestTool");
		List<Measurement> measurements = new ArrayList<>();

		Measurement measurement1 = new Measurement();
		Measurement measurement2 = new Measurement();
		Measurement measurement3 = new Measurement();

		AverageMeasurement avgM = new AverageMeasurement();

		Point3D p1 = new Point3D(1, 1, 1);
		Point3D p2 = new Point3D(2, 2, 2);
		Point3D p3 = new Point3D(3, 3, 3);

		measurement1.setPoint(p1);
		measurement1.setRotation(new Quaternion((float) 0, (float) 0, (float) 0, (float) 1));
		measurement2.setPoint(p2);
		measurement2.setRotation(new Quaternion((float) 0, (float) 0, (float) 0, (float) 1));
		measurement3.setPoint(p3);
		measurement3.setRotation(new Quaternion((float) 0, (float) 0, (float) 0, (float) 1));

		avgM.setPoint(p2);

		measurements.add(measurement1);
		measurements.add(measurement2);
		measurements.add(measurement3);

		testTool.addMeasurement(measurement1);
		testTool.addMeasurement(measurement2);
		testTool.addMeasurement(measurement3);

		testTool.setAverageMeasurement(avgM);

	}

}

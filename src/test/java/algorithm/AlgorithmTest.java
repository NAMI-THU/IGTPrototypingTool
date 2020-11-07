package algorithm;

import com.jme3.math.Quaternion;
import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static java.lang.Math.PI;
import static java.lang.Math.sin;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlgorithmTest {

    AverageMeasurement firstAverangeMeasurement;
    AverageMeasurement secondAverangeMeasurement;
    Measurement firstMeasurement;
    Measurement secondMeasurement;
    private DataProcessor processor;
    private DataService dataService;
    private ToolMeasure testTool;

    @Test
    /**
     * {@link DataProcessor}
     * {@link DataProcessor#getAverageMeasurement(AverageMeasurement)}
     */
    public void getAverageMeasurementIsCorrect() {
        setUpData();

        AverageMeasurement result = testTool.getAverageMeasurement();

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

        double result = testTool.getAverageMeasurement().getJitter();
        System.out.println("result:" + result);
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

        Quaternion result = testTool.getAverageRotation();

        assertTrue(result.equals(new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));

    }

    @Test
    /**
     * {@link DataProcessor} {@link DataProcessor#getAccuracyRotation(Measurement)}
     */
    public void getAccuracyRotationIsCorrect() {

        setUpDataAccuracy();
        Quaternion expectedRotation = new Quaternion((float) 0, (float) sin(PI / 4), (float) 0, (float) sin(PI / 4));

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
        Quaternion quaternion1 = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
        Quaternion quaternion2 = new Quaternion(0.0f, (float) sin(PI / 4), 0, (float) sin(PI / 4));
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

        Point3D p1 = new Point3D(1, 1, 1);
        Point3D p2 = new Point3D(2, 2, 2);
        Point3D p3 = new Point3D(3, 3, 3);

        measurement1.setPoint(p1);
        measurement1.setRotation(new Quaternion(0.0f, 0.0f, 0.0f, 1.0f));
        measurement2.setPoint(p2);
        measurement2.setRotation(new Quaternion(0.0f, 0.0f, 0.0f, 1.0f));
        measurement3.setPoint(p3);
        measurement3.setRotation(new Quaternion(0.0f, 0.0f, 0.0f, 1.0f));


        measurements.add(measurement1);
        measurements.add(measurement2);
        measurements.add(measurement3);

        testTool.addMeasurement(measurement1);
        testTool.addMeasurement(measurement2);
        testTool.addMeasurement(measurement3);

    }

}

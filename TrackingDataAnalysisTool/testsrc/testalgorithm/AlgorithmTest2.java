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
import algorithm.DataManager;
import algorithm.DataProcessor;
import algorithm.DataService;

import algorithm.ToolMeasure;


public class AlgorithmTest2 {

	private DataService dataService;

	
	@Test
	/**
	 * {@link DataService}
	 * {@link DataService#loadNextData(List)}
	 */
	public void test() {

		int countToGetNext = 3;
		List<ToolMeasure> toolMeasures = dataService.loadNextData(countToGetNext);

		for (int i = 0; i < toolMeasures.size(); i++) {
			ToolMeasure toolMeasure = toolMeasures.get(i);
			String tool = toolMeasure.getName();
			AverageMeasurement averageMeasurement = toolMeasure.getAverageMeasurement();
			double jitter = averageMeasurement.getError();
			BoxPlot boxPlot = averageMeasurement.getBoxPlot();
			double min = boxPlot.getMin();
			double max = boxPlot.getMax();
			double median = boxPlot.getMedian();
			double q1 = boxPlot.getQ1();
			double q3 = boxPlot.getQ3();
			System.out.println("Tool: " + tool + "Jitter: " + jitter + "BoxPlot Werte: Min: " + min + " 1.Q : "+ q1 + " Median: " + median + " 3.Q: " +q3+ " Max: " + max );
		}
	}

}

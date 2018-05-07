package algorithm;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import algorithm.DataProcessor;
import algorithm.Measurement;
import algorithm.Point;
import algorithm.ToolMeasure;

public class AlgorithmTest3 {


		private DataProcessor processor;
		List<Point> firstPoints;
		List<Point> secondPoints;

		@Test
		public void getAccuracyCalculationIsCorrect() {

			setUpData();
			double expectedDistance = 1.7320508075688772;

			double result = processor.getAccuracy(expectedDistance, firstPoints, secondPoints);

			// keine Abweichung erwartet
			assertTrue(result == 0);
		}

		private void setUpData() {
			processor = new DataProcessor();

			firstPoints = new ArrayList<>();
			secondPoints = new ArrayList<>();

			Point p1 = new Point(1, 1, 1);
			Point p4 = new Point(2, 2, 2);

			firstPoints.add(p1);
			secondPoints.add(p4);
		}

	}

}
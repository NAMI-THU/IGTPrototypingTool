package algorithm;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import algorithm.DataProcessor;
import javafx.geometry.Point3D;


public class AlgorithmTest3 {


		private DataProcessor processor;
		AverageMeasurement firstAverangeMeasurement;
		AverageMeasurement secondAverangeMeasurement;
		
		

		@Test
		public void getAccuracyIsCorrect() {

			setUpData();
			double expectedDistance = 1.7320508075688772;

			double result = processor.getAccuracy(expectedDistance, firstAverangeMeasurement, secondAverangeMeasurement);

			// keine Abweichung erwartet
			assertTrue(result == 0);
		}

		private void setUpData() {
			processor = new DataProcessor();
			
			firstAverangeMeasurement = new AverageMeasurement();
			secondAverangeMeasurement = new AverageMeasurement();
			
			Point3D p1 = new Point3D(1, 1, 1);
			Point3D p2 = new Point3D(2, 2, 2);
			
			firstAverangeMeasurement.setPoint(p1);
			secondAverangeMeasurement.setPoint(p2);

		
		}

	}


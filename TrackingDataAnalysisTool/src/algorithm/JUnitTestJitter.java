package algorithm;



//import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
//import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import algorithm.DataProcessor;
import algorithm.Measurement;
import algorithm.Point;
import algorithm.ToolMeasure;


class JUnitTestJitter {

		private DataProcessor processor;
		private ToolMeasure testTool;
		
		@Test
		public void jitterCalculationIsCorrect() {
			setUpData();
			
			ToolMeasure result = processor.getJitter(testTool);
			
			assertTrue(result.getAverageMeasurement().getPoints().get(0).getError() == 1.414213562373095);
			
		}
		
		private void setUpData() {
					
			processor = new DataProcessor();
			testTool = new ToolMeasure("TestTool");
			
			List<Point> points1 = new ArrayList<>();
			List<Point> points2 = new ArrayList<>();
			List<Point> points3 = new ArrayList<>();
			
			Measurement measurement1 = new Measurement();
			Measurement measurement2 = new Measurement();
			Measurement measurement3 = new Measurement();
			
			Point p1 = new Point(1,1,1);
			Point p2 = new Point(2,2,2);
			Point p3 = new Point(3,3,3);
			
			points1.add(p1);
			points2.add(p2);
			points3.add(p3);
			
			measurement1.setPoints(points1);
			measurement2.setPoints(points2);
			measurement3.setPoints(points3);
			
			testTool.addMeasurement(measurement1);
			testTool.addMeasurement(measurement2);
			testTool.addMeasurement(measurement3);

			testTool.setAverageMeasurement(measurement2);
		}

	}




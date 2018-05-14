package testInputOutput;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import inputOutput.CSVFileReader;
import inputOutput.Tool;

public class CSVFileReaderTest {


	@Test
	public void updateTest() {

		for (int i = 1; i <= 1; i++) {

			ArrayList<Tool> testlist = new ArrayList<Tool>();
			testlist = CSVFileReader.update();

			System.out.println("____________________________________");
			
		
			// print the tool list out
			for (int index = 0; index < testlist.size(); index++) {
				System.out.println("Point = " + testlist.get(index).getPoint() + ", rotation_x=" + testlist.get(index).getRotation_x() + ", rotation_y=" + testlist.get(index).getRotation_y() + ", rotation_z="
						+testlist.get(index).getRotation_z()+ ", rotation_r=" +testlist.get(index).getRotation_r() + ", valid=" + testlist.get(index).getValid() + ", timestamp=" + testlist.get(index).getTimestamp()
					+ ", name=" + testlist.get(index).getName());
				
			}
			
			
			

			
			
			

		}
	}

}

package testInputOutput;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.Test;

import inputOutput.CSVFileReader;
import inputOutput.Tool;

public class CSV_File_was_not_found {

	ArrayList<Tool> testlist = new ArrayList<Tool>();

	@Test
	public void updateTest() throws IOException {

		String path = "C:/Users/franz-lokal/logfile.csv";
		CSVFileReader myReader = new CSVFileReader();
		myReader.setPath(path);

		
		for (int i = 1; i <= 150; i++) {

			testlist = myReader.update();
			
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			if (testlist.isEmpty()) {
				break;
			}

		}

	}

}

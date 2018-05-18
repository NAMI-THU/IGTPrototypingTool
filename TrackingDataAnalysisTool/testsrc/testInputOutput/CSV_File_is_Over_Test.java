package testInputOutput;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.Test;

import inputOutput.CSVFileReader;
import inputOutput.Tool;

public class CSV_File_is_Over_Test {

	ArrayList<Tool> testlist = new ArrayList<Tool>();
	

	@Test
	public void updateTest() throws IOException {

		String path = "Q:/logfile_neu.csv";
		CSVFileReader.setPath(path);
		

		for (int i = 1; i <= 150; i++) {

			testlist = CSVFileReader.update();
			if (testlist.isEmpty()) {
				if(CSVFileReader.getLine_counter()==0){
					System.out.println("Data is empty!");
				}else{
					System.out.println("Update Method was only called " + i +" times because the file is finish");
				}
				
				break;
			}

		}

	}

}

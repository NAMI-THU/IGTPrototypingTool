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
	int help = testlist.size();

	@Test
	public void updateTest() throws IOException {

		Path path = Paths.get("Q:/logfile_neu.csv");

		for (int i = 1; i <= 200; i++) {

			testlist = CSVFileReader.update(path);
			if (testlist.get(0).getCoordinat().getX() == -100000) {
				System.out.println("Update wurde " + i
						+ " mal aufgerufen, da die Datei zu Ende ist.");
				break;
			}

		}

	}

}

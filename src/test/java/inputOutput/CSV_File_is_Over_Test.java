package inputOutput;

import inputOutput.CSVFileReader;
import inputOutput.Tool;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class CSV_File_is_Over_Test {

    ArrayList<Tool> testlist = new ArrayList<Tool>();


    @Test
    public void updateTest() throws IOException {

        String path = "C:/Users/franz-lokal/logfile.csv";
        CSVFileReader myReader = new CSVFileReader();
        myReader.setPath(path);


        for (int i = 1; i <= 150; i++) {

            testlist = myReader.update();
            if (testlist.isEmpty()) {
                inputOutput.ExceptionData.checkException();

                break;
            }

        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}

package inputOutput;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class CSVFileIsOverTest {

    @Test
    public void updateTest() throws IOException {

        String path = "C:/Users/franz-lokal/logfile.csv";
        CSVFileReader myReader = new CSVFileReader(path);

        for (int i = 1; i <= 150; i++) {
            ArrayList<Tool> testlist = myReader.update();
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

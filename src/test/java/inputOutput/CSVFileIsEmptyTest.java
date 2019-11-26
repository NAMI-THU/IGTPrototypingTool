package inputOutput;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class CSVFileIsEmptyTest {

    @Test
    public void updateTest() throws IOException {

        String path = "C:/Users/franz-lokal/logfile.csv";
        CSVFileReader myReader = new CSVFileReader();
        myReader.setPath(path);

        for (int i = 1; i <= 150; i++) {
            ArrayList<Tool> testList = myReader.update();
            if (testList.isEmpty()) {
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

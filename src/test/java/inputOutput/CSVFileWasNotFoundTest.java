package inputOutput;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class CSVFileWasNotFoundTest {

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

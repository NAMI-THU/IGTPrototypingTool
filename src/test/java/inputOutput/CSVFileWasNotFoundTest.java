package inputOutput;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.*;

public class CSVFileWasNotFoundTest {

    @Test
    public void updateTest() throws IOException {
        String path = "/file/does/not/exist.csv";
        try {
            CSVFileReader myReader = new CSVFileReader(path);
            myReader.update();
            fail();
        } catch (FileNotFoundException e) {
            return;
        } catch (Exception e) {
            fail();
        }
    }
}

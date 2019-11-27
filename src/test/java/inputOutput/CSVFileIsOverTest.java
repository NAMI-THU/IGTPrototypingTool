package inputOutput;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CSVFileIsOverTest {

    @Test
    public void updateTest() throws IOException {

        String path = Thread.currentThread()
                .getContextClassLoader()
                .getResource("testdata.csv")
                .getPath();
        CSVFileReader reader = new CSVFileReader(path);

        // The testdata.csv file has 121 records
        for (int i = 0; i < 121; i++) {
            assertFalse(reader.update().isEmpty());
        }

        assertTrue(reader.update().isEmpty());
    }
}

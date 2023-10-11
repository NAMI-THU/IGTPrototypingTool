package inputOutput;


import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class CSVFileReaderTest {

    @Test
    public void testRepeat() throws IOException {
        String path = Thread.currentThread()
                .getContextClassLoader()
                .getResource("multiple-tools.csv")
                .getPath();
        CSVFileReader file = new CSVFileReader(path);
        file.setRepeatMode(true);
        List<TempTool> record1stTime = file.update();

        file.update();
        file.update();
        file.update();

        List<TempTool> record2ndTime = file.update();
        for (int i = 0; i < record1stTime.size(); ++i) {
            assertTrue(record1stTime.get(i).equals(record2ndTime.get(i)));
        }
    }

    @Test
    public void testGetLast() throws IOException {
        String path = Thread.currentThread()
                .getContextClassLoader()
                .getResource("multiple-tools.csv")
                .getPath();
        CSVFileReader reader = new CSVFileReader(path);
        List<TempTool> records = reader.update();

        assertEquals(reader.getLastToolList(), records);
    }

    @Test
    public void testFileNotFound() {
        String path = "/file/does/not/exist.csv";
        try {
            CSVFileReader myReader = new CSVFileReader(path);
            myReader.update();
            fail("File does not exist - expected FileNotFoundException");
        } catch (FileNotFoundException e) {
            return;
        } catch (Exception e) {
            fail("Expected FileNotFoundException, got " + e.getMessage());
        }
    }

    @Test
    public void updateExactNumberOfRecords() throws IOException {

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

    @Test
    public void testEmptyFile() throws IOException {
        CSVFileReader myReader = new CSVFileReader(new StringReader(""));
        List<TempTool> result = myReader.update();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testOneTool() throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String path = loader.getResource("testdata.csv").getPath();
        CSVFileReader myReader = new CSVFileReader(path);
        List<TempTool> tempTools = myReader.update();

        // Sample data loaded from the first measurement of the sample data
        TempTool actual = new TempTool();
        actual.setData(2100502.0, 1.0, 77.987652886439, 12.3151565717349,
                445.369278569562, -0.438100248347888, 0.80324400339826,
                0.328079843044433, 0.23501246773358, "Tool0");
        TempTool tempTool = tempTools.get(0);

        assertTrue(tempTool.equals(actual));
    }

    @Test
    public void testMultipleTools() throws IOException {
        String path = Thread.currentThread()
                .getContextClassLoader()
                .getResource("multiple-tools.csv")
                .getPath();
        CSVFileReader myReader = new CSVFileReader(path);

        TempTool testTempTool1 = new TempTool();
        testTempTool1.setData(2100502, 1, 77.987652886439, 12.3151565717349,
                445.369278569562, -0.438100248347888, 0.80324400339826,
                0.328079843044433, 0.23501246773358, "Tool0");

        TempTool testTempTool2 = new TempTool();
        testTempTool2.setData(2100502, 1, 62.3730812318003, 5.13397572152332,
                436.379757362176, -0.469096638136285, 0.78435027049366,
                0.319787545528856, 0.249957842426255, "Tool1");

        // read csv-file
        List<TempTool> testList = myReader.update();

        // test tool1 correct import
        assertTrue(testTempTool1.equals(testList.get(0)));

        // test tool2 correct import
        assertTrue(testTempTool2.equals(testList.get(1)));
    }
}

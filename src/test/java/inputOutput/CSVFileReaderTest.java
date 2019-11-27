package inputOutput;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.*;

public class CSVFileReaderTest {

    @Test
    public void testRepeat() throws IOException {
        String path = Thread.currentThread()
                .getContextClassLoader()
                .getResource("multiple-tools.csv")
                .getPath();
        CSVFileReader file = new CSVFileReader(path);
        file.setRepeatMode(true);
        List<Tool> record1stTime = file.update();

        file.update();
        file.update();
        file.update();

        List<Tool> record2ndTime = file.update();
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
        List<Tool> records = reader.update();

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
        List<Tool> result = myReader.update();
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testOneTool() throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String path = loader.getResource("testdata.csv").getPath();
        CSVFileReader myReader = new CSVFileReader(path);
        List<Tool> tools = myReader.update();

        // Sample data loaded from the first measurement of the sample data
        Tool actual = new Tool();
        actual.setData(2100502.0, 1.0, 77.987652886439, 12.3151565717349,
                445.369278569562, -0.438100248347888, 0.80324400339826,
                0.328079843044433, 0.23501246773358, "Tool0");
        Tool tool = tools.get(0);

        assertTrue(tool.equals(actual));
    }

    @Test
    public void testMultipleTools() throws IOException {
        String path = Thread.currentThread()
                .getContextClassLoader()
                .getResource("multiple-tools.csv")
                .getPath();
        CSVFileReader myReader = new CSVFileReader(path);

        Tool testTool1 = new Tool();
        testTool1.setData(2100502, 1, 77.987652886439, 12.3151565717349,
                445.369278569562, -0.438100248347888, 0.80324400339826,
                0.328079843044433, 0.23501246773358, "Tool0");

        Tool testTool2 = new Tool();
        testTool2.setData(2100502, 1, 62.3730812318003, 5.13397572152332,
                436.379757362176, -0.469096638136285, 0.78435027049366,
                0.319787545528856, 0.249957842426255, "Tool1");

        // read csv-file
        List<Tool> testList = myReader.update();

        // test tool1 correct import
        assertTrue(testTool1.equals(testList.get(0)));

        // test tool2 correct import
        assertTrue(testTool2.equals(testList.get(1)));
    }
}

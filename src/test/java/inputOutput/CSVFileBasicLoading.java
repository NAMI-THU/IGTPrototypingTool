package inputOutput;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class CSVFileBasicLoading {

    private final double EPSILON = 1e-6;

    @Test
    public void test() throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String path = loader.getResource("testdata.csv").getPath();
        CSVFileReader myReader = new CSVFileReader(path);
        ArrayList<Tool> tools = myReader.update();

        // Sample data loaded from the first measurement of the sample data
        Tool tool = tools.get(0);
        assertEquals(tool.getTimestamp(), 2100502.0, EPSILON);
        assertEquals(tool.getName(), "Tool0");

        assertEquals(tool.getCoordinate().getX(), 77.987652886439, EPSILON);
        assertEquals(tool.getCoordinate().getY(), 12.3151565717349, EPSILON);
        assertEquals(tool.getCoordinate().getZ(), 445.369278569562, EPSILON);

        assertEquals(tool.getRotationX(), -0.438100248347888, EPSILON);
        assertEquals(tool.getRotationY(), 0.80324400339826, EPSILON);
        assertEquals(tool.getRotationZ(), 0.328079843044433, EPSILON);
        assertEquals(tool.getRotationR(), 0.23501246773358, EPSILON);
    }
}

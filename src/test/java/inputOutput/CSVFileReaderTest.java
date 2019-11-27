package inputOutput;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class CSVFileReaderTest {

    private static final double EPSILON = 1e-6;

    @Test
    public void updateTest() throws IOException {
        String path = Thread.currentThread()
                .getContextClassLoader()
                .getResource("multiple-tools.csv")
                .getPath();
        CSVFileReader myReader = new CSVFileReader(path);

        Tool testTool1 = new Tool();
        testTool1.setData(2100502, 1, 77.987652886439, 12.3151565717349,
                445.369278569562, -0.438100248347888, 0.80324400339826, 0.328079843044433, 0.23501246773358, "Tool0");

        Tool testTool2 = new Tool();
        testTool2.setData(2100502, 1, 62.3730812318003, 5.13397572152332,
                436.379757362176, -0.469096638136285, 0.78435027049366, 0.319787545528856, 0.249957842426255, "Tool1");

        // read csv-file
        ArrayList<Tool> testList = myReader.update();

        // test tool1 correct import
        testEquality(testTool1, testList.get(0));

        // test tool2 correct import
        testEquality(testTool2, testList.get(1));
    }

    private void testEquality(final Tool tool1, final Tool tool2) {
        assertEquals(tool1.getCoordinate().getX(), tool2.getCoordinate().getX(), EPSILON);
        assertEquals(tool1.getCoordinate().getY(), tool2.getCoordinate().getY(), EPSILON);
        assertEquals(tool1.getCoordinate().getZ(), tool2.getCoordinate().getZ(), EPSILON);
        assertEquals(tool1.getRotationX(), tool2.getRotationX(), EPSILON);
        assertEquals(tool1.getRotationY(), tool2.getRotationY(), EPSILON);
        assertEquals(tool1.getRotationZ(), tool2.getRotationZ(), EPSILON);
        assertEquals(tool1.getRotationR(), tool2.getRotationR(), EPSILON);
        assertEquals(tool1.getTimestamp(), tool2.getTimestamp(), EPSILON);
        assertEquals(tool1.getValid(), tool2.getValid(), EPSILON);
        assertEquals(tool1.getName(), tool2.getName());
    }
}

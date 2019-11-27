package inputOutput;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class CSVFileReaderTest {

    private ArrayList<Tool> testList = new ArrayList<>();

    @Test
    public void updateTest() throws IOException {

        String path = "C:/Users/franz-lokal/logfile.csv";
        CSVFileReader myReader = new CSVFileReader(path);

        Tool testtool1 = new Tool();
        testtool1.setData(188138.0, 1.0, 73.5664539062506, 75.3365062500002,
                -58.9611234374996, 0.0, 0.0, 0.0, 1.0, "Mega");

        Tool testtool2 = new Tool();
        testtool2.setData(188150.0, 1.0, -127.2664015625, -194.487728906251,
                34.1103312500008, 0.0, 0.0, 0.0, 1.0, "Geiler");

        Tool testtool3 = new Tool();
        testtool3.setData(188162.0, 1.0, -54.7602851562496, -259.4175390625,
                44.6227570312496, 0.0, 0.0, 0.0, 1.0, "Typ");

        // read csv-file
        for (int i = 1; i <= 1; i++) {
            testList = myReader.update();
        }

        // test too1 correct import
        assertEquals(true, testequality(testtool1, 0));

        // test too2 correct import
        assertEquals(true, testequality(testtool2, 1));

        // test too3 correct import
        assertEquals(true, testequality(testtool3, 2));
    }

    private boolean testequality(Tool testtool, int index) {
        boolean helptest = true;

        if (testtool.getCoordinate().getX() != testList.get(index)
                .getCoordinate().getX()) {
            helptest = false;
        } else if (testtool.getCoordinate().getY() != testList.get(index)
                .getCoordinate().getY()) {
            helptest = false;
        } else if (testtool.getCoordinate().getZ() != testList.get(index)
                .getCoordinate().getZ()) {
            helptest = false;
        } else if (testtool.getRotationX() != testList.get(index)
                .getRotationX()) {
            helptest = false;
        } else if (testtool.getRotationY() != testList.get(index)
                .getRotationY()) {
            helptest = false;
        } else if (testtool.getRotationZ() != testList.get(index)
                .getRotationZ()) {
            helptest = false;
        } else if (testtool.getRotationR() != testList.get(index)
                .getRotationR()) {
            helptest = false;
        } else if (testtool.getTimestamp() != testList.get(index).getTimestamp()) {
            helptest = false;
        } else if (testtool.getValid() != testList.get(index).getValid()) {
            helptest = false;
        } else if (!testtool.getName().equals(testList.get(index).getName())) {
            helptest = false;
        }

        return helptest;
    }
}

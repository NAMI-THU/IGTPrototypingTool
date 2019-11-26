package inputOutput;

import org.junit.Test;

import java.util.ArrayList;

public class CSVFileBasicLoading {

    @Test
    public void test() {
        String path = "C:/tools/logfile.csv";
        CSVFileReader myReader = new CSVFileReader();
        myReader.setPath(path);
        ArrayList<Tool> tools = myReader.update();
        System.out.println("Found " + tools.size() + " tools.");
        for (Tool t : tools) System.out.println("Name: " + t.getName());
        for (int i = 0; i < 100; i++) {
            tools = myReader.update();
            System.out.print("Data: ");
            for (Tool t : tools)
                System.out.print("{" + t.getName() + ":"
                        + t.getCoordinate().getX() + ";"
                        + t.getCoordinate().getY() + ";"
                        + t.getCoordinate().getZ() + "}");
            System.out.println();
        }
    }

}

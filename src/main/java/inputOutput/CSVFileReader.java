package inputOutput;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * This class reads in a 'CSV-file containing a variation of tools and
 * coordinates of their position in a room.  It files them, so the different
 * variable can be allocated to each single tool
 *
 * @author
 */
public class CSVFileReader extends TrackingDataSource {
    private static int exceptionNumber = 0;
    // initialize variable
    private int lineCounter = 0;
    // create tool list
    private String[] data = null;
    private int numberOfTools = 0;
    private String[] toolName = null;
    private String path;
    private boolean repeatMode = false;
    private BufferedReader csvFile = null;

    public CSVFileReader(BufferedReader reader) {
        csvFile = reader;
    }

    public CSVFileReader(String path) throws IOException {
        this(new BufferedReader(new InputStreamReader(
                new FileInputStream(path))));
    }

    public static int getExceptionNumber() {
        return exceptionNumber;
    }

    public boolean isRepeatModeOn() {
        return repeatMode;
    }

    // interface for the other groups

    public void setRepeatMode(boolean repeatMode) {
        this.repeatMode = repeatMode;
    }

    /**
     * this returns an ArrayList containing as many tools as are listed in the
     * CSV-file: when the CSV-file contains two tools then the method returns
     * an ArrayList containing the objects of two tools
     *
     * @return ArrayList of tools
     */
    public ArrayList<Tool> update() {

        // reader for CSV-file
        try {

            if ((lineCounter == 0) || (csvFile == null)) {
                lineCounter = 0;
                init();
            }

            if (csvFile.readLine() != null) {

                lineCounter++;
                match();
                return toolList;
            } else {
                if (repeatMode) {
                    csvFile.close();
                    csvFile = null;
                    lineCounter = 0;
                }
                return toolList;
            }

        } catch (IOException e) {
            exceptionNumber = 1;
            ExceptionData.checkException();

            return toolList;
        }
    }

    /**
     * the method init tells us the number of tools in the CSV-file by
     * splitting up the lines, one tool has 9 Variables.  the method also
     * assigns the tools to the toollist, that makes sure that the toollist
     * contains as many objects as there are tools available in the file
     *
     * @throws IOException
     */
    private void init() throws IOException {

        read();

        // find the number of the tools
        numberOfTools = (data.length) / 9;
        toolName = new String[numberOfTools];
        toolList = new ArrayList<Tool>();

        // creating tools depending on the number of tools and adding them to the
        // Tool list
        for (int i = 1, j = 0; i <= numberOfTools; i++, j = j + 9) {

            Tool tool = new Tool();
            toolList.add(tool);

            // Get the name of the tools from the Csv-file
            getName(data[j], (i - 1));
        }

        // decrease line_counter because next line has to be read
        lineCounter++;
        match();
    }

    /**
     * This method matches the values of the CSV-file to each Object of the
     * toollist to make sure, that each object contains the values that belong
     * to it
     *
     * @throws IOException
     */
    private void match() throws IOException {

        read();

        double[] dataNew = new double[data.length];

        for (int a = 0; a < data.length; a++) {
            // casting from string to double
            dataNew[a] = Double.parseDouble(data[a]);
        }

        for (int i = 0, j = 0; i < toolList.size(); i++, j = j + 9) {
            // assign the Values of the Csv-File to the Object
            toolList.get(i).setData(dataNew[j], dataNew[j + 1],
                    dataNew[j + 2], dataNew[j + 3], dataNew[j + 4],
                    dataNew[j + 5], dataNew[j + 6], dataNew[j + 7],
                    dataNew[j + 8], toolName[i]);
        }
    }

    /**
     * this method creates a file reader for the CSV-file which is found by the
     * method setPath()
     */
    private void read() {
        // create the file reader for the CSV data
        csvFile = null;
        try {
            // reader for CSV-file
            csvFile = new BufferedReader(new InputStreamReader(
                    new FileInputStream(path)));

            // splits the CSV-data by semicolon and saves the Values in an
            // array
            for (int j = 0; j <= lineCounter; j++) {
                String line = csvFile.readLine();
                data = line.split(";");

            }

        } catch (IOException e) {
            // error message output
            System.out.println("Read error " + e);

        }

    }

    /**
     * this method gets the name of the tool out of the CSV-file
     *
     * @param csv_name   contains name of the tool
     * @param index_name shows index of the tool to get the correct name for
     *                   each tool
     */
    private void getName(String csv_name, int index_name) {
        // find the tool name by splitting "timestamp_name"
        String[] name = csv_name.split("_");
        toolName[index_name] = name[1];

    }

    public int getLineCounter() {
        return lineCounter;
    }

    public void setLine_counter() {
        lineCounter = 0;
        toolList.clear();
    }
}

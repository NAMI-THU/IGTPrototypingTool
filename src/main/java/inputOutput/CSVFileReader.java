package inputOutput;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class reads in a 'CSV-file containing a variation of tools and
 * coordinates of their position in a room.  It files them, so the different
 * variable can be allocated to each single tool
 *
 * @author
 */
public class CSVFileReader extends AbstractTrackingDataSource {
    private static final int exceptionNumber = 0;
    private int recordNumber = 0;
    private final List<String> toolNames;
    private boolean repeatMode = false;
    private final List<CSVRecord> records;

    /**
     * Constructs a CSVFileReader from a Reader object.
     *
     * @param reader The reader to use.
     * @throws IOException
     */
    public CSVFileReader(final Reader reader) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT    // first use the default csv format
                .builder().setDelimiter(';')    // file uses ';' instead of ','
                .setHeader().build();         // file has a header
        CSVParser csvFile = new CSVParser(reader, format);
        records = csvFile.getRecords();

        // gets the names of all tools based on the TimeStamp header
        String header = "TimeStamp_";
        toolNames = csvFile.getHeaderNames()
                .stream()
                .filter(s -> s.startsWith(header))
                .map(s -> s.substring(header.length()))
                .collect(Collectors.toList());
    }

    /**
     * Constructs a CSVFileReader by reading a file from the filesystem.
     *
     * @param path The path to read.
     * @throws IOException If the file does not exist.
     */
    public CSVFileReader(final String path) throws IOException {
        this(new FileReader(path));
    }

    public static int getExceptionNumber() {
        return exceptionNumber;
    }

    public boolean isRepeatModeOn() {
        return repeatMode;
    }

    public void setRepeatMode(boolean repeatMode) {
        this.repeatMode = repeatMode;
    }

    public List<String> getToolNames() {
        return toolNames;
    }

    /**
     * this returns an ArrayList containing as many tools as are listed in the
     * CSV-file: when the CSV-file contains two tools then the method returns
     * an ArrayList containing the objects of two tools
     *
     * @return ArrayList of tools
     */
    public ArrayList<Tool> update() {
        toolList = new ArrayList<>(toolNames.size());
        if (recordNumber >= records.size() && repeatMode) {
            recordNumber = 0;
        } else if (recordNumber >= records.size()) {
            return toolList;
        }

        CSVRecord currentRecord = records.get(recordNumber);
        for (String toolName : toolNames) {
            toolList.add(parseTool(currentRecord, toolName));
        }
        recordNumber += 1;
        return toolList;
    }

    @Override
    public void closeConnection() {}

    private Tool parseTool(final CSVRecord record, final String toolName) {
        String[] headers = {"TimeStamp_", "Valid_", "X_", "Y_", "Z_",
                "QX_", "QY_", "QZ_", "QR_"};
        Tool t = new Tool();

        double[] data = Arrays.stream(headers)
                .map(s -> s + toolName)
                .map(record::get)
                .mapToDouble(Double::parseDouble)
                .toArray();

        t.setData(data[0], data[1], data[2], data[3], data[4], data[5],
                data[6], data[7], data[8], toolName);
        return t;
    }

    /**
     * TODO interrupt update loop without closing app completely
     * */
    public void stopUpdate() {
    }
}

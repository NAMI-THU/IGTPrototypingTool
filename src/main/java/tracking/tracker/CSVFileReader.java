package tracking.tracker;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import tracking.Measurement;
import util.Quaternion;
import util.Vector3D;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class reads in a 'CSV-file containing a variation of tools and
 * coordinates of their position in a room.  It files them, so the different
 * variable can be allocated to each single tool
 *
 * @author
 */
public class CSVFileReader implements TrackingSource {
    private int filePointer = 0;
    private List<String> toolNames = new ArrayList<>();
    private boolean repeatMode = false;
    private List<CSVRecord> records = new ArrayList<>();

    private final String fileName;

    /**
     * Creates a new CSV FileReader
     *
     * @param fileName the path to the csv file to read
     */
    public CSVFileReader(String fileName, boolean loopFile){
        this.fileName = fileName;
        this.repeatMode = loopFile;
    }


    public boolean isRepeatModeOn() {
        return repeatMode;
    }

    public void setLoopFile(boolean repeatMode) {
        this.repeatMode = repeatMode;
    }

    /**
     * this returns an ArrayList containing as many tools as are listed in the
     * CSV-file: when the CSV-file contains two tools then the method returns
     * an ArrayList containing the objects of two tools
     *
     * @return ArrayList of tools
     */
    public Map<String, Measurement> measure() {
        var map = new HashMap<String, Measurement>();
        if (filePointer >= records.size() && repeatMode) {
            filePointer = 0;
        } else if (filePointer >= records.size()) {
            return map;
        }

        CSVRecord currentRecord = records.get(filePointer);
        for (String toolName : toolNames) {
            map.put(toolName, parseTool(currentRecord, toolName));
        }
        filePointer += 1;
        return map;
    }

    @Override
    public void disconnect() {}

    @Override
    public boolean isConnected() {
        return records != null;
    }

    @Override
    public void connect() throws IOException{
        CSVFormat format = CSVFormat.DEFAULT    // first use the default csv format
                .builder().setDelimiter(';')    // file uses ';' instead of ','
                .setHeader()
                .setAllowMissingColumnNames(true)
                .build();         // file has a header
        CSVParser csvFile = new CSVParser(new FileReader(fileName), format);
        records = csvFile.getRecords();

        // gets the names of all tools based on the TimeStamp header
        String header = "TimeStamp_";
        toolNames = csvFile.getHeaderNames()
                .stream()
                .filter(s -> s.startsWith(header))
                .map(s -> s.substring(header.length()))
                .collect(Collectors.toList());

        // Don't need to wait for the disconnect call when we can close it directly here
        csvFile.close();
    }

    private Measurement parseTool(final CSVRecord record, final String toolName) {
        String[] headers = {"TimeStamp_", "Valid_", "X_", "Y_", "Z_",
                "QX_", "QY_", "QZ_", "QR_"};

        double[] data = Arrays.stream(headers)
                .map(s -> s + toolName)
                .map(record::get)
                .mapToDouble(Double::parseDouble)
                .toArray();

        return new Measurement(toolName, data[0],
                new Vector3D(data[2], data[3], data[4]),
                new Quaternion(data[8], data[5], data[6], data[7]));
//        return new Measurement(toolName, data[0], data[2], data[3], data[4], data[5], data[6], data[7], data[8]);
    }
}

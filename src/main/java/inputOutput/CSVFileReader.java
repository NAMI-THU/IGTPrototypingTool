package inputOutput;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;

import inputOutput.Exception_Window;
import userinterface.MeasurementView;

/**
 * this class reads in a 'CSV-file containing a variation of tools and coordinates of their position in a room.
 * it files them, so the different variable can be allocated to each single tool
 * @author 
 *
 */
public class CSVFileReader extends TrackingDataSource {
	// initialize variable
	private int line_counter = 0;
	private String line = null;
	private String[] data = null;
	// create tool list
	
	private int number_of_tools = 0;
	private String[] toolname = null;
	private String path;
	private static int exception_number = 0;
	private boolean repeatMode = false;
	

	public boolean isRepeatModeOn() {
		return repeatMode;
	}
	public void setRepeatMode(boolean repeatMode) {
		this.repeatMode = repeatMode;
	}
	
	private BufferedReader csv_file = null;

	// interface for the other groups
	
	
	/**
	 * this returns an ArrayList containing as many tools as are listed in the CSV-file:
	 * when the CSV-file contains two tools then the method returns an ArrayList containing the objects of two tools
	 * @return ArrayList of tools
	 */
	public ArrayList<Tool> update() {

		// reader for CSV-file
		try {
			
			if ((line_counter == 0) || (csv_file == null)) {
				line_counter = 0;
				init();
			}
						
			if (csv_file.readLine() != null) {
				
				line_counter++;
				match();
				return toollist;
			} 
			
			else {
				if(repeatMode)
				{
					csv_file.close();
					csv_file = null;
					line_counter = 0;
				}
				return toollist;
			}

		} catch (IOException e) {
			exception_number = 1;
			ExceptionData.checkException();

			return toollist;
		}

	}
	/**
	 * the method init tells us the number of tools in the CSV-file by splitting up the lines, one tool has 9 Variables.
	 * the method also assigns the tools to the toollist, that makes sure that the toollist contains as many objects 
	 * as there are tools available in the file
	 * @throws IOException
	 */

	private void init() throws IOException {

		read();
		
		// find the number of the tools
		number_of_tools = (data.length) / 9;
		toolname = new String[number_of_tools];
		toollist = new ArrayList<Tool>();

		// creating tools depending on the number of tools and adding them to the
		// Tool list
		for (int i = 1, j = 0; i <= number_of_tools; i++, j = j + 9) {

			Tool tool = new Tool();
			toollist.add(tool);

			// Get the name of the tools from the Csv-file
			getName(data[j], (i - 1));
		}

		// decrease line_counter because next line has to be read
		line_counter++;
		match();
	}

	/**
	 * this method matches the values of the CSV-file to each Object of the toollist to make sure, that each object 
	 * contains the values that belong to it
	 * @throws IOException
	 */
	private void match() throws IOException {

		read();

		double[] data_new = new double[data.length];

		for (int a = 0; a < data.length; a++) {
			// casting from string to double
			data_new[a] = Double.parseDouble(data[a]);
		}

		for (int i = 0, j = 0; i < toollist.size(); i++, j = j + 9) {
			// assign the Values of the Csv-File to the Object
			toollist.get(i).setData(data_new[j], data_new[j + 1],
					data_new[j + 2], data_new[j + 3], data_new[j + 4],
					data_new[j + 5], data_new[j + 6], data_new[j + 7],
					data_new[j + 8], toolname[i]);

		}

	}

	/**
	 * this method creates a file reader for the CSV-file which is found by the method setPath()
	 */
	private void read() {
		// create the file reader for the CSV data
		csv_file = null;
		try {

			// reader for CSV-file
			csv_file = new BufferedReader(new InputStreamReader(
					new FileInputStream(path)));

		} catch (Exception e) {
		}

		try {
			// splits the CSV-data by semicolon and saves the Values in an
			// array
			for (int j = 0; j <= line_counter; j++) {

				line = csv_file.readLine();
				data = line.split(";");

			}

		} catch (IOException e) {
			// error message output
			System.out.println("Read error " + e);

		}

	}

	/**
	 * this method gets the name of the tool out of the CSV-file
	 * @param csv_name contains name of the tool
	 * @param index_name shows index of the tool to get the correct name for each tool
	 */
	private void getName(String csv_name, int index_name) {
		// find the tool name by splitting "timestamp_name"
		String[] name = csv_name.split("_");
		toolname[index_name] = name[1];

	}

	/**
	 * this sets the path of the location from the CSV-file
	 * @param abspath is a string of a path you need to chose to be able to read in exactly the file you want to
	 */
	public void setPath(String abspath) {
		System.out.println("Path set: " + path);
		path = abspath;
		//userinterface.Gui.setTexttoloaded();
	}

	public int getLine_counter() {
		return line_counter;
	}
	public static int getException_number(){
		return exception_number;
		}
	public void setLine_counter() {
		line_counter=0;
		toollist.clear();
	}
}

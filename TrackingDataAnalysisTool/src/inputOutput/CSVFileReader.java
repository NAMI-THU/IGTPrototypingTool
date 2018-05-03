package inputOutput;

import java.io.*;
import java.util.ArrayList;

public class CSVFileReader extends Interface {
	// initialize variable
	private static int line_counter = 0;
	private static String line = null;
	private static String[] data = null;
	// create tool list
	private static ArrayList<Tool> toollist = new ArrayList<Tool>();
	static int number_of_tools = 0;
	private static String[] toolname = null;

	// interface for the other groups
	public static ArrayList update() {

		if (line_counter == 0) {
			init();
		} else {
			read();
		}
		// return value of tool list
		return toollist;

	}

	private static void init() {

		readline();
		// find the number of the tools
		number_of_tools = (data.length) / 9;
		toolname = new String[number_of_tools];

		// creating tools depends on the number of tools and add them to the
		// Tool list
		for (int i = 1, j = 0; i <= number_of_tools; i++, j = j + 9) {

			Tool tool = new Tool();
			toollist.add(tool);

			// Get the name of the tools from the Csv-file
			getName(data[j], (i - 1));
		}

		// decrease line_counter because next line has to be read
		line_counter++;
		read();
	}

	private static void read() {

		readline();

		double[] data_new = new double[data.length];

		for (int a = 0; a < data.length; a++) {
			// casting from string to double
			data_new[a] = Double.parseDouble(data[a]);
		}

		for (int i = 0, j = 0; i < number_of_tools; i++, j = j + 9) {
			// set the Values of the Csv-File to the Object
			toollist.get(i).setData(data_new[j], data_new[j + 1], data_new[j + 2], data_new[j + 3], data_new[j + 4],
					data_new[j + 5], data_new[j + 6], data_new[j + 7], data_new[j + 8], toolname[i]);

		}
		// print the tool list out
		for (int index = 0; index < toollist.size(); index++) {
			System.out.println(toollist.get(index));
		}

		// decrease line_counter because next line has to be read
		line_counter++;

	}

	private static void readline() {
		// create the file reader for the CSV data
		BufferedReader csv_file = null;
		try {

			// reader for CSV-file
			csv_file = new BufferedReader(new InputStreamReader(new FileInputStream("Q:\\logfile_nochmal_neu.csv")));

		} catch (Exception e) {
		}

		try {
			// split the CSV-data with semicolon and saves the Values in an
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

	private static void getName(String csv_name, int index_name) {
		// find the tool name with the splitted timestamp name
		String[] name = csv_name.split("_");
		toolname[index_name] = name[1];

	}

	// Test method for checking the programm
	public static void main(String[] args) {

		for (int i = 1; i <= 5; i++) {

			ArrayList<Tool> testlist = new ArrayList<Tool>();
			testlist = update();

			System.out.println("____________________________________");

		}
	}

}

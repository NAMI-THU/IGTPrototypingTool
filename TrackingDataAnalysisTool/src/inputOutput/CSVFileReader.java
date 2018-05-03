package inputOutput;

import java.io.*;
import java.util.ArrayList;

public class CSVFileReader extends Interface {

	private static int line_counter = 0;
	private static String line = null;
	private static String[] data = null;
	private static ArrayList<Tool> toollist = new ArrayList<Tool>();
	static int number_of_tools = 0;

	public static ArrayList update() {

		if (line_counter == 0) {
			init();
		} else {
			read();
		}
		return toollist;

	}

	private static void init() {

		readline();
		number_of_tools = (data.length) / 9;

		for (int i = 1, j = 0; i <= number_of_tools; i++, j = j + 9) {

			Tool tool = new Tool();
			toollist.add(tool);
		}

		line_counter++;
		read();
	}

	private static void read() {

		readline();

		double[] data_new = new double[data.length];

		for (int a = 0; a < data.length; a++) {

			data_new[a] = Double.parseDouble(data[a]);
		}

		
		
		for (int i = 0, j = 0; i < number_of_tools; i++, j = j + 9) {

			toollist.get(i).setData(data_new[j], data_new[j + 1], data_new[j + 2], data_new[j + 3], data_new[j + 4],
					data_new[j + 5], data_new[j + 6], data_new[j + 7], data_new[j + 8], "tool" + (i + 1));

		}

		for (int index = 0; index < toollist.size(); index++) {
			System.out.println(toollist.get(index));
		}

		line_counter++;

	}

	private static void readline() {

		BufferedReader csv_file = null;
		try {

			csv_file = new BufferedReader(new InputStreamReader(new FileInputStream("Q:\\logfile_neu.csv")));

		} catch (Exception e) {
		}

		try {

			for (int j = 0; j <= line_counter; j++) {
				line = csv_file.readLine();
				data = line.split(";");

			}

		} catch (IOException e) {
			System.out.println("Read error " + e);

		}

	}

	public static void main(String[] args) {

		for (int i = 1; i <= 5; i++) {

			ArrayList<Tool> testlist = new ArrayList<Tool>();
			testlist = update();

			System.out.println("____________________________________");

		}
	}

}

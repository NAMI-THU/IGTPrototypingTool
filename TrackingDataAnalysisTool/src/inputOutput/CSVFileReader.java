package inputOutput;

import java.io.*;
import java.util.ArrayList;

public class CSVFileReader extends Interface {

	static int line_counter = 0;
	static String line = null;
	static String[] data = null;
	static ArrayList<Tool> toollist = null;
	static int number_of_tools = 0;

	private static void init() {

		readline();
		number_of_tools = (data.length) / 9;
		read();
	}

	private static void read() {

		toollist = new ArrayList<Tool>();

		readline();

		double[] data_new = new double[data.length];

		if (line_counter >= 1) {
			for (int a = 0; a < data.length; a++) {

				data_new[a] = Double.parseDouble(data[a]);
			}

			for (int i = 1, j = 0; i <= number_of_tools; i++, j = j + 9) {

				Tool tool = new Tool(data_new[j], data_new[j + 1], data_new[j + 2], data_new[j + 3], data_new[j + 4],
						data_new[j + 5], data_new[j + 6], data_new[j + 7], data_new[j + 8], "tool" + i);

				toollist.add(tool);
			}

			for (int index = 0; index < toollist.size(); index++) {
				System.out.println(toollist.get(index));
			}
		}

		line_counter++;

	}

	private static void readline() {

		BufferedReader csv_file = null;
		try {

			csv_file = new BufferedReader(new InputStreamReader(new FileInputStream("Q:\\logfile.csv")));

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

		for (int i = 0; i <= 5; i++) {

			init();
			System.out.println("____________________________________");

		}
	}

}

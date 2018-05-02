package inputOutput;

import java.io.*;

public class CSVFileReader extends Interface {

	static int i = 0;

	private static void init() {

	}

	private static void read() {
		String line = null;
		String[] data = null;

		BufferedReader csv_file = null;
		try {

			csv_file = new BufferedReader(new InputStreamReader(new FileInputStream("Q:\\logfile.csv")));

		} catch (Exception e) {
		}

		try {

			
			
			for (int j = 0; j <= i; j++) {
				line = csv_file.readLine();
				data = line.split(";");

			}

			double[] data_new = new double[data.length];

			if(i==1){
			for (int a = 0; a < data.length; a++) {

				data_new[a] = Double.parseDouble(data[a]);
			}

			int number_of_tools = (data.length) / 9;

			// System.out.println(number_of_tools);

			//for (int i = 1; i <= number_of_tools; i++) {

				Tool tool = new Tool(data_new[0],data_new[1],data_new[2],data_new[3],data_new[4],data_new[5],data_new[6],data_new[7],data_new[8],"Test");

			//}

			}
			for (int n = 0; n < data.length; n++) {
				System.out.println(data[n]);
			}
			i++;
		} catch (IOException e) {
			System.out.println("Read error " + e);

		}

	}

	public static void main(String[] args) {

		for (int i = 0; i <= 1; i++) {

			read();
			System.out.println("____________________________________");

		}
	}

}

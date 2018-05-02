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
			
			
			int number_of_tools = (data.length)/9;
			
			//System.out.println(number_of_tools);
			
			
			
			
			
			
			
			
			
			
			
			
			
			

			for (int n = 0; n < data.length; n++) {
				System.out.println(data[n]);
			}
			i++;
		} catch (IOException e) {
			System.out.println("Read error " + e);

		}

	}

	public static void main(String[] args) {

		for (int i = 0; i <= 0; i++) {

			read();
			System.out.println("____________________________________");

		}
	}

}

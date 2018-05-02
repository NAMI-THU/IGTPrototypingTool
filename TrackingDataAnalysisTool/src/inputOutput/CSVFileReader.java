package inputOutput;

import java.io.*;
import java.util.ArrayList;

public class CSVFileReader extends Interface {

	static int line_counter = 0;

	private static void init() {

	}

	private static void read() {
		String line = null;
		String[] data = null;
		ArrayList<Tool> toollist = new ArrayList<Tool>();
		
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

			double[] data_new = new double[data.length];

			if (line_counter == 1) {
				for (int a = 0; a < data.length; a++) {

					data_new[a] = Double.parseDouble(data[a]);
				}

				int number_of_tools = (data.length) / 9;

				// System.out.println(number_of_tools);

				 for (int i = 1, j=0; i <= number_of_tools; i++,j=j+9) {
					 
					 
				
				
				Tool tool = new Tool(data_new[j], data_new[j+1], data_new[j+2], data_new[j+3], data_new[j+4], data_new[j+5],
						data_new[j+6], data_new[j+7], data_new[j+8], "tool"+i);
				
				toollist.add(tool);
			 }

			
				 for(int index = 0; index< toollist.size();index++){
				 System.out.println(toollist.get(index));
				 }
			}
		//	for (int n = 0; n < data.length; n++) {
			//	System.out.println(data[n]);
			//}
			line_counter++;
		} catch (IOException e) {
			System.out.println("Read error " + e);

		}

	}

	public static void main(String[] args) {

		for (int i = 0; i <= 5; i++) {

			read();
			System.out.println("____________________________________");

		}
	}

}

package inputOutput;

import java.io.*;

public class CSVFileReader extends Interface {

	
	static int i = 0;

	
	
	
	
	
	
	
	
	private static void init() {

		String line = null;
		String [] data = null;
		
		
		BufferedReader csv_file = null; 
		try {
			
			
			
			csv_file=new BufferedReader(new InputStreamReader(new FileInputStream("U:\\Semester4\\Projekt\\logfile.csv")));

			
		} catch (Exception e) {
		}

		try {
			

			for(int j = 0; j <= i ; j++){
				 line = csv_file.readLine();
				 data = line.split(";");
			  
				
			  
					}
			
			
			for(int n= 0; n < data.length; n++){
				System.out.println(data[n]);
				i++;}

		} catch (IOException e) {
			System.out.println("Read error " + e);

		}
 
	}

	
	
	
	
	
	
	private void read(){
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {

		init();
		System.out.println("____________________________________");
		init();
		

	}

}

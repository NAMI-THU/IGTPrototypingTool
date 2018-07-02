package inputOutput;

import userinterface.ExceptionWindow;
/**
 * this class detects errors when reading in the CSV-file
 * @author 
 *
 */

public class ExceptionData {

	private static int exception_number;
	
	
	/**
	 * when there is an error this method checks out if the CSV-file either is empty,
	 * the file has already been read completely or if the data was not found
	 * for each case there is a number which tells us what kind of error exists
	 */
	public static void checkException() {

		exception_number = CSVFileReader.getException_number();
		
		switch (exception_number) {

		case 0:
			if (CSVFileReader.getLine_counter() == 0) {
				ExceptionWindow.setExceptionText("Data is empty");
				

			} else {
				ExceptionWindow.setExceptionText("End of file");
				
			}
			break;

		case 1:// must be changed to the right methodname of group userinterface
				// when implemented
			ExceptionWindow.setExceptionText("Data was not found");
			
			break;
		default: testInputOutput.Exception_Window.startExceptionWindow();

		}

	}

}

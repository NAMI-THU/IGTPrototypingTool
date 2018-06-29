package inputOutput;

import userinterface.ExceptionWindow;

public class ExceptionData {

	private static int exception_number;
	
	public static void checkException() {

		exception_number = CSVFileReader.getException_number();
		
		switch (exception_number) {

		case 0:
			if (CSVFileReader.getLine_counter() == 0) {
				ExceptionWindow.setExceptionText("Data is empty");
				

			} else {
				ExceptionWindow.setExceptionText("end of file");
				
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

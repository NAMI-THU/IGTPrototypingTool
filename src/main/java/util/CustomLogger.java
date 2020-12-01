package util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class CustomLogger {
	private static FileHandler txtFile;
	private static SimpleFormatter formatter;

	public static void setup() throws IOException {
		Logger log = Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
		txtFile = new FileHandler("logging.log", true);
		formatter = new SimpleFormatter();
		txtFile.setFormatter(formatter);
		log.addHandler(txtFile);
	}
}

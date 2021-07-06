package util;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class CustomLogger {
    private static FileHandler txtFile;
    private static SimpleFormatter formatter;
    private static boolean consoleOn = true;

    /**
     * Setup a log file in addition to standard console output
     * @throws IOException
     */
    public static void setup() throws IOException {
        Logger log = Logger.getLogger(CustomLogger.class.getName());
        txtFile = new FileHandler("logging%u.log", true);
        formatter = new SimpleFormatter();
        txtFile.setFormatter(formatter);
        log.addHandler(txtFile);
    }

    /**
     * Static method to write to custom logger from any other class.
     * @param level of severity
     * @param message to be shown in log
     */
    public static void log(Level level, String message) {
        Logger.getLogger(CustomLogger.class.getName()).log(level, message);
    }

    /**
     * Static method to write to custom logger from any other class with more details.
     * @param level of severity
     * @param message to be shown in log
     * @param t throwable
     */
    public static void log(Level level, String message, Throwable t) {
        Logger.getLogger(CustomLogger.class.getName()).log(level, message, t);
    }

    /**
     * Stop stream to log file
     */
    public static void closeLogger() {
        Handler[] handlers = Logger.getLogger(CustomLogger.class.getName()).getHandlers();
        if(handlers.length > 0 && handlers[0] instanceof FileHandler) {
            handlers[0].close();
        }
    }

    /**
     * Enable/disable console output
     */
    public static void changeConsoleOutput() {
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers.length == 0) {
            // enable console logging output
            rootLogger.addHandler(new ConsoleHandler());
            consoleOn = true;
        } else if(handlers[0] instanceof ConsoleHandler) {
            // suppress the logging output to the console
            rootLogger.removeHandler(handlers[0]);
            consoleOn = false;
        }
    }

    public static boolean isConsoleOn() {
        return consoleOn;
    }
}

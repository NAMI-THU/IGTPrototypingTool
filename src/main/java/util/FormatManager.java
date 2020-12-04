package util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * This class includes custom formats to use in this project.
 *
 */
public class FormatManager {
    public static String toString(Double input) {
        DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
        return df.format(input);
    }

    public static String toString(Object input) {
        DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
        return df.format(input);
    }
}

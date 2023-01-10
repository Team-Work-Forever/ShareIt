package shareit.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

public class DatePattern {

    /**
     * Defines a global DateTimeFormatter with format ("dd-MM-yyyy")
     * @return DateTimeFormatter
     */
    private static DateTimeFormatter getDateTimeFormatter() {

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            .toFormatter();

        formatter = formatter.withLocale(Locale.getDefault());

        return formatter;

    }

    /**
     * Insert date with the format dd-MM-yyyy
     * @param date value String to convert to LocalDate
     * @return LocalDate with format dd-MM-yyyy
     */
    public static LocalDate insertDate(String date) {
        return LocalDate.parse(date, getDateTimeFormatter());
    }

    /**
     * Convert date to the format dd-MM-yyyy
     * @param date value LocalDate to convert to String
     * @return String with format dd-MM-yyyy
     */
    public static String convertDate(LocalDate date) {
        return date.format(getDateTimeFormatter());
    }

}

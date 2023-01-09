package shareit.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

public class DatePattern {

    private static DateTimeFormatter getDateTimeFormatter() {

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            .toFormatter();

        formatter = formatter.withLocale(Locale.getDefault());

        return formatter;

    }

    /**
     * Please Insert this date with the format dd-MM-yyyy
     * @param date
     * @return
     */
    public static LocalDate insertDate(String date) {
        return LocalDate.parse(date, getDateTimeFormatter());
    }

    public static String convertDate(LocalDate date) {
        return date.format(getDateTimeFormatter());
    }

}

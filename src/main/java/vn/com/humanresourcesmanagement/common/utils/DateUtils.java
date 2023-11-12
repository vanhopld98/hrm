package vn.com.humanresourcesmanagement.common.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateUtils {

    private DateUtils() {
    }

    public static final String DD_MM_YYYY = "dd/MM/yyyy";
    public static final String HH_MM_SS = "HH:mm:ss";

    public static String convertLocalDateTimeToString(LocalDateTime localDateTime, String formatDate) {
        if (localDateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatDate);
        return localDateTime.format(formatter);
    }

    public static String convertLocalDateToString(LocalDate localDate, String formatDate) {
        if (localDate == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatDate);
        return localDate.format(formatter);
    }

}

package com.exposure.handlers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Class handling the dates represented in the application
 */
public class DateHandler {

    private static final SimpleDateFormat sdf =
            new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    /**
     * Translates the date object into the required string format
     * @param date the date to be translated
     * @return the translated date in string format
     */
    public static String convertToString(Date date) {
        return sdf.format(date);
    }

    /**
     * Translates the date string into a date object
     * @param dateString the date string to be translated
     * @return the translated date as a Date object
     * @throws ParseException exception is thrown if the date cannot be parsed
     */
    public static Date convertToDate(String dateString) throws ParseException {
        return sdf.parse(dateString);
    }

    /**
     * Calculates number of years between two different date objects
     * @param earliest the earlier of the two dates
     * @param latest the later of the two dates
     * @return the number of years between the two different data objects
     */
    public static int yearsBetween(Date earliest, Date latest) {
        Calendar a = new GregorianCalendar();
        a.setTime(earliest);

        Calendar b = new GregorianCalendar();
        b.setTime(latest);

        int years = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);

        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) >
                        b.get(Calendar.DATE))) {
            years--;
        }

        return years;
    }

    /**
     * Generates a timestamp for the current date
     * @return a timestamp
     */
    public static String generateTimestamp() {
        return new SimpleDateFormat("dd_MM_yyyy_HH-mm-ss", Locale.ENGLISH).format(
                new Date());
    }
}

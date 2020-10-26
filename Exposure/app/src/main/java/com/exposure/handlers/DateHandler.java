package com.exposure.handlers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateHandler {
    private static SimpleDateFormat sdf =
            new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    /* Translate the date object into the required string format */
    public static String convertToString(Date date) {
        return sdf.format(date);
    }

    /* Translate the date string into a date object. Throws parse exception if it can't parse it*/
    public static Date convertToDate(String dateString) throws ParseException {
        return sdf.parse(dateString);
    }

    /* Calculates number of years between two different date objects */
    public static int yearsBetween(Date earliest, Date latest) {
        Calendar a = new GregorianCalendar();
        a.setTime(earliest);

        Calendar b = new GregorianCalendar();
        b.setTime(latest);

        int years = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);

        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            years--;
        }

        return years;
    }
}

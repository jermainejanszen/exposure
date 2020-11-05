package com.exposure;

import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.*;
import static com.exposure.handlers.DateHandler.*;

public class DateHandlerTest {

    @Test
    public void nullConvertToStringTest() {
        assertNull(convertToString(null));
    }

    @Test
    public void convertToStringTestA() {
        Date date = new Date(965180151100L);
        assertEquals("02/08/2000", convertToString(date));
    }

    @Test
    public void convertToStringTestB() {
        Date date = new Date(1465180151100L);
        assertEquals("06/06/2016", convertToString(date));
    }

    @Test
    public void nullConvertToDateTest() {
        try {
            assertNull(convertToDate(null));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void invalidConvertToDateStringTest() {
        boolean caughtException = false;

        try {
            assertNull(convertToDate("invalid date string"));
        } catch (ParseException pe) {
            caughtException = true;
        }

        assertTrue(caughtException);
    }

    @Test
    public void convertToDateTestA() {
        Date date = null;

        try {
            date = convertToDate("02/08/2000");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assertNotNull(date);
        assertEquals(965138400000L, date.getTime());
    }

    @Test
    public void convertToDateTestB() {
        Date date = null;

        try {
            date = convertToDate("06/06/2016");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assertNotNull(date);
        assertEquals(1465135200000L, date.getTime());
    }

    @Test
    public void nullEarliestDateYearsBetweenTest() {
        boolean caughtException = false;

        try {
            yearsBetween(null, new Date());
        } catch (IllegalArgumentException ie) {
            caughtException = true;
        }

        assertTrue(caughtException);
    }

    @Test
    public void nullLatestDateYearsBetweenTest() {
        boolean caughtException = false;

        try {
            yearsBetween(new Date(), null);
        } catch (IllegalArgumentException ie) {
            caughtException = true;
        }

        assertTrue(caughtException);
    }

    @Test
    public void nullEarliestDateAndLatestDateYearsBetweenTest() {
        boolean caughtException = false;

        try {
            yearsBetween(null, null);
        } catch (IllegalArgumentException ie) {
            caughtException = true;
        }

        assertTrue(caughtException);
    }

    @Test
    public void yearsBetweenTest() {
        Date earliest = new Date(965180151100L);
        Date latest = new Date(1465180151100L);

        assertEquals(15, yearsBetween(earliest, latest));
    }
}

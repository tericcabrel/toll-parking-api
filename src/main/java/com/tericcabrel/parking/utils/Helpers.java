package com.tericcabrel.parking.utils;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Contains useful methods used in the project
 */
public class Helpers {
    /**
     * Add or update a HashMap item
     *
     * @param errors instance of HashMap<String, List<String>> to update
     * @param field field to set as key in HashMap
     * @param message content to be set as value of the key
     *
     * @return updated instance of HashMap<String, List<String>>
     */
    public static HashMap<String, List<String>> updateErrorHashMap(
        HashMap<String, List<String>> errors, String field, String message
    ) {
        if (errors.containsKey(field)) {
            List<String> strings = errors.get(field);
            strings.add(message);

            errors.put(field, strings);
        } else {
            List<String> strings = new ArrayList<>();
            strings.add(message);

            errors.put(field, strings);
        }

        return errors;
    }

    /**
     * @param startTime Start time
     * @param endTime End time
     *
     * @return the difference between two times in hour
     */
    public static double calculateDuration(Date startTime, Date endTime) {
        long diffInSeconds = (endTime.getTime() - startTime.getTime()) / 1000;

        double remaining = diffInSeconds % 3600;
        double differenceInHour = (diffInSeconds - remaining) / 3600;

        // Round to 2 decimals
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.UP);

        return Double.valueOf(df.format(differenceInHour + (remaining / 3600)));
    }

    /**
     * @param year Year
     * @param month Month (0 based)
     * @param day Day
     * @param hour Hour
     * @param minute Minute
     * @param second Second
     *
     * @return Instance of Date
     */
    public static Date createDateFromValue(int year, int month, int day, int hour, int minute, int second) {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * @param date format a date to MM/dd/yyyy HH:mm
     *
     * @return the date formatted
     */
    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM ");
        SimpleDateFormat hourFormat = new SimpleDateFormat(" 'at' HH:mm");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

        return dateFormat.format(date) + Integer.valueOf(yearFormat.format(date)) + hourFormat.format(date);
    }

    /**
     * @param dateISOString String
     *
     * @return Date
     */
    public static Date isoStringToDate(String dateISOString) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        try {
            return dateFormat.parse(dateISOString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}

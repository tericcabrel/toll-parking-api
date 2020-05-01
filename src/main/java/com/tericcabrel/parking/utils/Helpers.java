package com.tericcabrel.parking.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
     * @param date format a date to MM/dd/yyyy HH:mm
     *
     * @return the date formatted
     */
    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM ");
        SimpleDateFormat hourFormat = new SimpleDateFormat(" 'at' HH:mm");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

        return dateFormat.format(date) + (Integer.valueOf(yearFormat.format(date)) - 1900) + hourFormat.format(date);
    }
}

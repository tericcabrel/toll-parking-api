package com.tericcabrel.parking.utils;

import java.util.ArrayList;
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
}

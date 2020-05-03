package com.tericcabrel.parking.utils;

import org.junit.jupiter.api.*;

import java.text.ParseException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HelpersTest {

    @Test
    void createDateFromValue() {
        Date date = Helpers.createDateFromValue(2020, Calendar.MAY, 1, 22, 40, 15);

        assertThat(date).isInstanceOf(Date.class);
        assertThat(date.toString()).isIn("Fri May 01 22:40:15 WAT 2020", "Fri May 01 22:40:15 CEST 2020");
    }

    @Test
    void calculateDuration() {
        Date startTime = Helpers.createDateFromValue(2020, Calendar.MAY, 1, 11, 3, 4);
        Date endTime = Helpers.createDateFromValue(2020, Calendar.MAY, 1, 14, 36, 4);

        double hour = Helpers.calculateDuration(startTime, endTime); // 3.55

        assertThat(hour).isGreaterThan(3.5);
    }

    @Test
    void formatDate() {
        Date startTime = Helpers.createDateFromValue(2020, Calendar.MAY, 1, 11, 3, 4);

        String dateString = Helpers.formatDate(startTime);

        assertThat(dateString).isNotNull();
        assertThat(dateString).isEqualTo("01 May 2020 at 11:03");
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.OrderAnnotation.class)
    class UpdateErrorHashMap {
        HashMap<String, List<String>> errors;

        @BeforeAll
        void beforeAll() {
            errors = new HashMap<>();

            errors.put("username", Arrays.asList("Error Username 1", "Error Username 2"));
            errors.put("email", Collections.singletonList("Error Email 1"));
        }

        @Test
        @Order(1)
        void addMessageInUnExistingFieldError() {
            Helpers.updateErrorHashMap(errors, "input", "Error Input 1");

            assertThat(errors.size()).isEqualTo(3);
            assertThat(errors.get("input")).hasSize(1);
            assertThat(errors.get("input")).contains("Error Input 1");
        }

        @Test
        @Order(2)
        void addMessageInExistingFieldError() {
            Helpers.updateErrorHashMap(errors, "input", "Error Input 2");

            assertThat(errors.size()).isEqualTo(3);
            assertThat(errors.get("input")).hasSize(2);
            assertThat(errors.get("input")).contains("Error Input 2");
        }
    }

    @Test
    void isoStringToDate() {
        String isoDate = "2020-05-02T16:23:59.000";

        Date result = Helpers.isoStringToDate(isoDate);

        assertThat(result).isNotNull();
    }

    @Test
    void isoStringToDateThrowException() {
        String isoDate = "2020-05-02 16:23:59.000";
        Date result = Helpers.isoStringToDate(isoDate);

        assertThat(result).isNull();
    }
}
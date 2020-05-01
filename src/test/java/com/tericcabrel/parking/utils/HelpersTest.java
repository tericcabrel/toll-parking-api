package com.tericcabrel.parking.utils;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class HelpersTest {

    @Test
    void calculateDuration() {
        Date startTime = new Date(2020, Calendar.MAY, 1, 11, 3, 4);
        Date endTime = new Date(2020, Calendar.MAY, 1, 14, 36, 4);

        double hour = Helpers.calculateDuration(startTime, endTime); // 3.55

        assertThat(hour).isGreaterThan(3.5);
    }

    @Test
    void formatDate() {
        Date startTime = new Date(2020, Calendar.MAY, 1, 11, 3, 4);

        String dateString = Helpers.formatDate(startTime);

        assertThat(dateString).isNotNull();
        assertThat(dateString).isEqualTo("01 May 2020 at 11:03");
    }
}
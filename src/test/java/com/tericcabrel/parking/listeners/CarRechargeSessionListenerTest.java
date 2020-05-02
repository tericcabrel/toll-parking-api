package com.tericcabrel.parking.listeners;

import com.tericcabrel.parking.events.OnCarRechargeSessionCompleteEvent;
import com.tericcabrel.parking.models.dbs.CarRechargeSession;
import com.tericcabrel.parking.models.dbs.CarType;
import com.tericcabrel.parking.models.dbs.Customer;
import com.tericcabrel.parking.models.enums.GenderEnum;
import com.tericcabrel.parking.utils.Helpers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ActiveProfiles("test")
@SpringBootTest
class CarRechargeSessionListenerTest {
    @MockBean
    private CarRechargeSessionListener carRechargeSessionListener;

    @Test
    void doThrowIllegalArgumentException() {
        Customer customer = Customer.builder()
            .email("test@customer.com")
            .name("Test Customer")
            .phone("+46455343554")
            .gender(GenderEnum.MALE)
            .carType(CarType.builder().name("CAR_TYPE").build())
            .build();

        CarRechargeSession carRechargeSession = CarRechargeSession.builder()
            .startTime(Helpers.createDateFromValue(2020, Calendar.MAY, 2, 21,33,10))
            .endTime(Helpers.createDateFromValue(2020, Calendar.MAY, 2, 23,33,10))
            .price(234)
            .build();

        doThrow(new IllegalArgumentException("Boom")).when(carRechargeSessionListener).sendEmail(any());

        assertThrows(IllegalArgumentException.class, () -> {
            carRechargeSessionListener.sendEmail(new OnCarRechargeSessionCompleteEvent(customer, carRechargeSession));
        });
    }
}
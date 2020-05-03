package com.tericcabrel.parking.listeners;

import com.tericcabrel.parking.events.OnCarRechargeSessionCompleteEvent;
import com.tericcabrel.parking.models.dbs.CarRechargeSession;
import com.tericcabrel.parking.models.dbs.CarType;
import com.tericcabrel.parking.models.dbs.Customer;
import com.tericcabrel.parking.models.enums.GenderEnum;
import com.tericcabrel.parking.utils.Helpers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Calendar;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CarRechargeSessionListenerTest {
    @InjectMocks
    private CarRechargeSessionListener carRechargeSessionListener;

    @Mock
    private Environment environment;

    @Mock
    private JavaMailSender mailSender;

    @Test
    void doThrowIllegalArgumentException() {
        Customer customer = Customer.builder()
            // .email("test@customer.com")
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


        when(environment.getProperty(anyString(), anyString())).thenReturn("Property");
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        carRechargeSessionListener.sendEmail(new OnCarRechargeSessionCompleteEvent(customer, carRechargeSession));

        verify(environment, times(2)).getProperty(anyString(), anyString());
    }
}
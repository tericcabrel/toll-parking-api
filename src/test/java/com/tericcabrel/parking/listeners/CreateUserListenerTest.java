package com.tericcabrel.parking.listeners;

import com.tericcabrel.parking.events.OnCreateUserCompleteEvent;
import com.tericcabrel.parking.models.dbs.User;
import com.tericcabrel.parking.models.enums.GenderEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserListenerTest {
    @InjectMocks
    private CreateUserListener createUserListener;

    @Mock
    private Environment environment;

    @Mock
    private JavaMailSender mailSender;

    @Test
    void publishEvent() {
        User user = User.builder()
            // .email("test@test.com") Missing this property throw IllegalArgumentException which is what we want to tests
            .name("Test User")
            .password("password")
            .gender(GenderEnum.MALE)
            .enabled(true)
            .build();

        when(environment.getProperty(anyString(), anyString())).thenReturn("Property");
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        createUserListener.sendEmail(new OnCreateUserCompleteEvent(user, "rawPassword"));

        verify(environment, times(2)).getProperty(anyString(), anyString());
    }
}

package com.tericcabrel.parking.listeners;

import com.tericcabrel.parking.events.OnCreateUserCompleteEvent;
import com.tericcabrel.parking.models.dbs.User;
import com.tericcabrel.parking.models.enums.GenderEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ActiveProfiles("test")
@SpringBootTest
class CreateUserListenerTest {
    @MockBean
    private CreateUserListener createUserListener;

    @Test
    void publishEvent() {
        User user = User.builder()
            .email("test@test.com")
            .name("Test User")
            .password("password")
            .gender(GenderEnum.MALE)
            .enabled(true)
            .build();

        doThrow(new IllegalArgumentException("Boom")).when(createUserListener).sendEmail(any());

        assertThrows(IllegalArgumentException.class, () -> createUserListener.sendEmail(new OnCreateUserCompleteEvent(user, "rawPassword")));
    }

}
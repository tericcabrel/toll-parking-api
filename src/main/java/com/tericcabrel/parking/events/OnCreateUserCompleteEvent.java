package com.tericcabrel.parking.events;

import com.tericcabrel.parking.models.dbs.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;

/**
 * Event fires when a new user is created
 */
@Getter
@Accessors(chain = true)
public class OnCreateUserCompleteEvent extends ApplicationEvent {
    private User user;

    private String rawPassword;

    public OnCreateUserCompleteEvent(User user, String rawPassword) {
        super(user);

        this.user = user;
        this.rawPassword = rawPassword;
    }
}

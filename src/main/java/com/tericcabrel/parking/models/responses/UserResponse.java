package com.tericcabrel.parking.models.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tericcabrel.parking.models.dbs.User;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true)
public class UserResponse {
    private User data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserResponse(@JsonProperty("data") User data) {
        this.data = data;
    }
}

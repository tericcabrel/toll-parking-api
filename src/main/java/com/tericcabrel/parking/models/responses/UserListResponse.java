package com.tericcabrel.parking.models.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tericcabrel.parking.models.dbs.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Accessors(chain = true)
public class UserListResponse {
    private List<User> data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserListResponse(@JsonProperty("data") List<User> data) {
        this.data = data;
    }
}

package com.tericcabrel.parking.models.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tericcabrel.parking.models.dbs.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RoleResponse {
    private Role data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RoleResponse(@JsonProperty("data") Role data) {
        this.data = data;
    }
}

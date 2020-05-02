package com.tericcabrel.parking.models.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tericcabrel.parking.models.dbs.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Accessors(chain = true)
public class RoleListResponse {
    private List<Role> data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RoleListResponse(@JsonProperty("data") List<Role> data) {
        this.data = data;
    }
}

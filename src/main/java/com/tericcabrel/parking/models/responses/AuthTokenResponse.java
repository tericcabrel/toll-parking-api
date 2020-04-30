package com.tericcabrel.parking.models.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AuthTokenResponse {
    private AuthToken data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public AuthTokenResponse(@JsonProperty("data") AuthToken data) {
        this.data = data;
    }
}



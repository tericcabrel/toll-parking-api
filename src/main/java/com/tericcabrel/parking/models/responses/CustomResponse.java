package com.tericcabrel.parking.models.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class CustomResponse {
    private HashMap<String, Object> data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CustomResponse(@JsonProperty("data") HashMap<String, Object> data) {
        this.data = data;
    }
}

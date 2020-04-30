package com.tericcabrel.parking.models.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;

@Getter
@Setter
@Accessors(chain = true)
public class ConstraintViolationResponse {
    private HashMap<String, HashMap<String, String>> data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ConstraintViolationResponse(@JsonProperty("data") HashMap<String, HashMap<String, String>> data) {
        this.data = data;
    }
}

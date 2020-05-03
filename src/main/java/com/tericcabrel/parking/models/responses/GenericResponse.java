package com.tericcabrel.parking.models.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;

@Getter
public class GenericResponse {
    private Map<String, Object> data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public GenericResponse(@JsonProperty("data") Map<String, Object> data) {
        this.data = data;
    }
}

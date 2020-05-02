package com.tericcabrel.parking.models.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;

@Getter
@Accessors(chain = true)
public class InvalidDataResponse {
    private HashMap<String, HashMap<String, List<String>>> data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public InvalidDataResponse(@JsonProperty("data") HashMap<String, HashMap<String, List<String>>> data) {
        this.data = data;
    }
}

package com.tericcabrel.parking.models.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tericcabrel.parking.models.dbs.CarType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class CarTypeListResponse {
    private List<CarType> data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CarTypeListResponse(@JsonProperty("data") List<CarType> data) {
        this.data = data;
    }
}

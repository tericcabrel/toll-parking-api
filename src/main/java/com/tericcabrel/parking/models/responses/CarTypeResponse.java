package com.tericcabrel.parking.models.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tericcabrel.parking.models.dbs.CarType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CarTypeResponse {
    private CarType data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CarTypeResponse(@JsonProperty("data") CarType data) {
        this.data = data;
    }
}

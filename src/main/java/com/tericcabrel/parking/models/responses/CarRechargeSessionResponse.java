package com.tericcabrel.parking.models.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tericcabrel.parking.models.dbs.CarRechargeSession;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true)
public class CarRechargeSessionResponse {
    private CarRechargeSession data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CarRechargeSessionResponse(@JsonProperty("data") CarRechargeSession data) {
        this.data = data;
    }
}

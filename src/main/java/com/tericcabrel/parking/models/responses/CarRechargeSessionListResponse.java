package com.tericcabrel.parking.models.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tericcabrel.parking.models.dbs.CarRechargeSession;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Accessors(chain = true)
public class CarRechargeSessionListResponse {
    private List<CarRechargeSession> data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CarRechargeSessionListResponse(@JsonProperty("data") List<CarRechargeSession> data) {
        this.data = data;
    }
}

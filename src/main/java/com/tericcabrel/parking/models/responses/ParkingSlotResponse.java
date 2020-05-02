package com.tericcabrel.parking.models.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tericcabrel.parking.models.dbs.ParkingSlot;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true)
public class ParkingSlotResponse {
    private ParkingSlot data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ParkingSlotResponse(@JsonProperty("data") ParkingSlot data) {
        this.data = data;
    }
}

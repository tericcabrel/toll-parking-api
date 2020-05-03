package com.tericcabrel.parking.models.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tericcabrel.parking.models.dbs.ParkingSlot;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Accessors(chain = true)
public class ParkingSlotListResponse {
    private List<ParkingSlot> data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ParkingSlotListResponse(@JsonProperty("data") List<ParkingSlot> data) {
        this.data = data;
    }
}

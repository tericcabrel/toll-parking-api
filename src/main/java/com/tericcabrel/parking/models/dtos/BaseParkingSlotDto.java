package com.tericcabrel.parking.models.dtos;

import com.tericcabrel.parking.models.enums.ParkingSlotStateEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseParkingSlotDto {
    protected String state;

    /**
     * @return State of the parking slot in enum type
     */
    public ParkingSlotStateEnum getParkingSlotStateEnum() {
        return state.equals("FREE") ? ParkingSlotStateEnum.FREE : ParkingSlotStateEnum.BUSY;
    }
}

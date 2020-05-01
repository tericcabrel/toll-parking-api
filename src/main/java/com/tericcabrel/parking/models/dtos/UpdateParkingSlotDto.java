package com.tericcabrel.parking.models.dtos;

import com.tericcabrel.parking.models.dbs.CarType;
import com.tericcabrel.parking.models.enums.ParkingSlotStateEnum;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@Data
public class UpdateParkingSlotDto {
    private String label;

    @Pattern(regexp = "FREE|BUSY", message = "The value for state must be FREE or BUSY")
    private String state;

    @NotNull(message = "Pricing policy is required")
    @Valid
    private PricingPolicyDto pricingPolicy;

    private String  carTypeId;

    private CarType carType;

    @Builder
    public UpdateParkingSlotDto(String label, String state, String carTypeId, PricingPolicyDto pricingPolicyDto)
    {
        this.label = label;
        this.state = state;
        this.carTypeId = carTypeId;
        this.pricingPolicy = pricingPolicyDto;
    }

    /**
     * @return State of the parking slot in enum type
     */
    public ParkingSlotStateEnum getParkingSlotStateEnum() {
        return state.equals("FREE") ? ParkingSlotStateEnum.FREE : ParkingSlotStateEnum.BUSY;
    }
}

package com.tericcabrel.parking.models.dtos;

import com.tericcabrel.parking.models.dbs.CarType;
import com.tericcabrel.parking.models.enums.ParkingSlotStateEnum;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@Data
public class CreateParkingSlotDto {
    @NotBlank(message = "The name is required")
    private String label;

    @Pattern(regexp = "FREE|BUSY", message = "The value for state must be FREE or BUSY")
    @NotBlank(message = "The state is required")
    private String state;

    @NotNull(message = "Pricing policy is required")
    @Valid
    private PricingPolicyDto pricingPolicyDto;

    @NotBlank(message = "This field is required")
    private String  carTypeId;

    private CarType carType;

    @Builder
    public CreateParkingSlotDto(String label, String state, String carTypeId, PricingPolicyDto pricingPolicyDto)
    {
        this.label = label;
        this.state = state;
        this.carTypeId = carTypeId;
        this.pricingPolicyDto = pricingPolicyDto;
    }

    /**
     * @return State of the parking slot in enum type
     */
    public ParkingSlotStateEnum getParkingSlotStateEnum() {
        return state.equals("FREE") ? ParkingSlotStateEnum.FREE : ParkingSlotStateEnum.BUSY;
    }
}

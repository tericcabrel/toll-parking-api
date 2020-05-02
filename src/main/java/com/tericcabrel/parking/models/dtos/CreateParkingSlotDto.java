package com.tericcabrel.parking.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tericcabrel.parking.models.dbs.CarType;
import com.tericcabrel.parking.models.enums.ParkingSlotStateEnum;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class CreateParkingSlotDto {
    @NotBlank(message = "The name is required")
    private String label;

    @Pattern(regexp = "FREE|BUSY", message = "The value for state must be FREE or BUSY")
    @NotBlank(message = "The state is required")
    private String state;

    @NotNull(message = "Pricing policy is required")
    @Valid
    private PricingPolicyDto pricingPolicy;

    @NotBlank(message = "This field is required")
    private String  carTypeId;

    private CarType carType;

    @Builder
    public CreateParkingSlotDto(String label, String state, String carTypeId, CarType carType, PricingPolicyDto pricingPolicyDto)
    {
        this.label = label;
        this.state = state;
        this.carTypeId = carTypeId;
        this.pricingPolicy = pricingPolicyDto;
        this.carType = carType;
    }

    /**
     * @return State of the parking slot in enum type
     */
    @JsonIgnore
    public ParkingSlotStateEnum getParkingSlotStateEnum() {
        return state.equals("FREE") ? ParkingSlotStateEnum.FREE : ParkingSlotStateEnum.BUSY;
    }
}

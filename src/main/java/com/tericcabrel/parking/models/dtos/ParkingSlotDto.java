package com.tericcabrel.parking.models.dtos;

import com.tericcabrel.parking.models.dbs.PricingPolicy;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@Data
public class ParkingSlotDto {
    @NotBlank(message = "The name is required")
    private String label;

    @Pattern(regexp = "FREE|BUSY", message = "The value for state must be FREE or BUSY")
    @NotBlank(message = "The state is required")
    private String state;

    @NotNull(message = "Pricing policy is required")
    @Valid
    private PricingPolicyDto pricingPolicyDto;

    @Builder
    public ParkingSlotDto(String label, String state, PricingPolicyDto pricingPolicyDto)
    {
        this.label = label;
        this.state = state;
        this.pricingPolicyDto = pricingPolicyDto;
    }
}

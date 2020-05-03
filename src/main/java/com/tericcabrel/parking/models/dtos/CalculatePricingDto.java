package com.tericcabrel.parking.models.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class CalculatePricingDto {
    @Size(min = 1, message = "At least one role's parameter is required")
    @NotNull(message = "This field is required")
    private Map<String, Double> parameters;

    @Builder
    public CalculatePricingDto(Map<String, Double> parameters) {
        this.parameters = parameters;
    }
}

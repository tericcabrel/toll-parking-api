package com.tericcabrel.parking.models.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class PricingPolicyDto {
    @Size(min = 1, message = "At least one role's parameter is required")
    @NotNull(message = "This field is required")
    private Map<String, Double> parameters;

    @NotBlank(message = "This field is required")
    private String evaluation;

    @Builder
    public PricingPolicyDto(Map<String, Double> parameters, String evaluation) {
        this.parameters = parameters;
        this.evaluation = evaluation;
    }
}

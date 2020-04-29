package com.tericcabrel.parking.models.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Data
public class CarTypeDto {
    @NotBlank(message = "The name is required")
    private String name;

    @Builder
    public CarTypeDto(String name) {
        this.name = name;
    }
}

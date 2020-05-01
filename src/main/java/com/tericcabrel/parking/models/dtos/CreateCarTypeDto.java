package com.tericcabrel.parking.models.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Data
public class CreateCarTypeDto {
    @Size(min = 3, message = "Must be at least 3 characters")
    @NotBlank(message = "The name is required")
    private String name;

    @Builder
    public CreateCarTypeDto(String name) {
        this.name = name;
    }
}

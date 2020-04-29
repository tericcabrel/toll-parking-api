package com.tericcabrel.parking.models.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Data
public class RoleDto {
    @NotBlank(message = "The name is required")
    private String name;

    private String description;

    @Builder
    public RoleDto(String name, String description) {
        this.name = name;
        this.description = description;
    }
}

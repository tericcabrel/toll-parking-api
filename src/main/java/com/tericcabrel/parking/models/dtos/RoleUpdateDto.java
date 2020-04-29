package com.tericcabrel.parking.models.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Data
public class RoleUpdateDto {
    @NotBlank(message = "The userId is required")
    private String userId;

    @NotEmpty(message = "The field must have at least one item")
    private String[] roles;

    @Builder
    public RoleUpdateDto(String userId, String[] roles) {
        this.userId = userId;
        this.roles = roles;
    }
}

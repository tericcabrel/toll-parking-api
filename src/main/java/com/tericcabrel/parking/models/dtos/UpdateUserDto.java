package com.tericcabrel.parking.models.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UpdateUserDto extends BaseUserDto {
    private String name;

    private boolean enabled;

    public UpdateUserDto(String name, boolean enabled, String gender) {
        this.name = name;
        this.enabled = enabled;
        this.gender = gender;
    }
}

package com.tericcabrel.parking.models.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UpdateUserDto extends BaseUserDto {
    private String name;

    private int enabled;

    public UpdateUserDto() {
        this.enabled = -1;
    }

    @Builder
    public UpdateUserDto(String name, int enabled, String gender) {
        this.name = name;
        this.enabled = enabled;
        this.gender = gender;
    }
}

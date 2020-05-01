package com.tericcabrel.parking.models.dtos;

import com.tericcabrel.parking.models.enums.GenderEnum;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public abstract class BaseUserDto {
    @NotBlank(message = "This field is required")
    protected String gender;

    /**
     * @return Gender of user in enum type
     */
    public GenderEnum getGenderEnum() {
        if (gender.equals("MALE")) {
            return GenderEnum.MALE;
        } else if (gender.equals("FEMALE")) {
            return GenderEnum.FEMALE;
        }

        return GenderEnum.OTHER;
    }
}

package com.tericcabrel.parking.models.dtos;

import com.tericcabrel.parking.models.dbs.CarType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class CustomerDto extends BaseUserDto {
    @NotBlank(message = "The name is required")
    private String name;

    @Email(message = "Email address is not valid")
    @NotBlank(message = "The email address is required")
    private String email;

    @NotBlank(message = "The phone is required")
    private String phone;

    @NotBlank(message = "This field is required")
    private String  carTypeId;

    private CarType carType;

    @Builder
    public CustomerDto(String name, String gender, String email, String phone, String carTypeId) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.carTypeId = carTypeId;
    }
}

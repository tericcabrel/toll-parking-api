package com.tericcabrel.parking.models.dtos;

import com.tericcabrel.parking.models.dbs.CarType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class UpdateCustomerDto extends BaseUserDto {
    private String name;

    private String email;

    private String phone;

    private String  carTypeId;

    private CarType carType;

    @Builder
    public UpdateCustomerDto(String name, String gender, String email, String phone, String carTypeId) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.carTypeId = carTypeId;
    }
}

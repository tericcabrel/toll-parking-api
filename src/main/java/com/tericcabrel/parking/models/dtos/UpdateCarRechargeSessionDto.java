package com.tericcabrel.parking.models.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Data
public class UpdateCarRechargeSessionDto {
    private Date endTime;

    private float price;

    @Builder
    public UpdateCarRechargeSessionDto(Date endTime, float price) {
        this.endTime = endTime;
        this.price = price;
    }
}

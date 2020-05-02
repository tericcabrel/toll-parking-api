package com.tericcabrel.parking.models.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@NoArgsConstructor
@Data
public class UpdateCarRechargeSessionDto {
    @NotNull(message = "This field is required")
    private Date endTime;

    // TODO Greather than Zero
    private double price;

    @Builder
    public UpdateCarRechargeSessionDto(Date endTime, double price) {
        this.endTime = endTime;
        this.price = price;
    }
}

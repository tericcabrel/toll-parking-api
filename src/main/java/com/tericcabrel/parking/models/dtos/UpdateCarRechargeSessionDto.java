package com.tericcabrel.parking.models.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@NoArgsConstructor
@Data
public class UpdateCarRechargeSessionDto {
    @NotBlank(message = "This field is required")
    private Date endTime;

    // TODO Greather than Zero
    private float price;

    @Builder
    public UpdateCarRechargeSessionDto(Date endTime, float price) {
        this.endTime = endTime;
        this.price = price;
    }
}

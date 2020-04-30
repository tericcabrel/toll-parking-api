package com.tericcabrel.parking.models.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@NoArgsConstructor
@Data
public class CreateCarRechargeSessionDto {
    @NotBlank(message = "This field is required")
    private Date startTime;

    private Date endTime;

    private float price;

    @NotBlank(message = "This field is required")
    private String parkingSlotId;

    @NotBlank(message = "This field is required")
    private String customerId;

    @Builder
    public CreateCarRechargeSessionDto(Date startTime, Date endTime, float price, String parkingSlotId, String customerId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.parkingSlotId = parkingSlotId;
        this.customerId = customerId;
    }
}

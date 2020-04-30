package com.tericcabrel.parking.models.dtos;

import com.tericcabrel.parking.models.dbs.Customer;
import com.tericcabrel.parking.models.dbs.ParkingSlot;
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

    private ParkingSlot parkingSlot;

    private Customer customer;


    @Builder
    public CreateCarRechargeSessionDto(
        Date startTime, Date endTime, float price, ParkingSlot parkingSlot, Customer customer
    ) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.parkingSlot = parkingSlot;
        this.customer = customer;
    }
}

package com.tericcabrel.parking.models.dtos;

import com.tericcabrel.parking.models.dbs.Customer;
import com.tericcabrel.parking.models.dbs.ParkingSlot;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class CreateCarRechargeSessionDto {
    @NotBlank(message = "This field is required")
    private Date startTime;

    private Date endTime;

    private float price;

    @NotBlank(message = "This field is required")
    private String customerId;

    private Customer customer;

    private ParkingSlot parkingSlot;

    @Builder
    public CreateCarRechargeSessionDto(
        Date startTime, Date endTime, float price, Customer customer
    ) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.customer = customer;
    }
}

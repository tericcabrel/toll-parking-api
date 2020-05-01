package com.tericcabrel.parking.models.dtos;

import com.tericcabrel.parking.models.dbs.Customer;
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
    private String customerId;

    private Customer customer;


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

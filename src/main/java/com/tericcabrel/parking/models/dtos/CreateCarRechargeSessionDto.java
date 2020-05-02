package com.tericcabrel.parking.models.dtos;

import com.tericcabrel.parking.models.dbs.Customer;
import com.tericcabrel.parking.models.dbs.ParkingSlot;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class CreateCarRechargeSessionDto {
    @NotBlank(message = "This field is required")
    private String customerId;

    private Customer customer;

    private ParkingSlot parkingSlot;

    @Builder
    public CreateCarRechargeSessionDto(Customer customer, String customerId) {
        this.customer = customer;
        this.customerId = customerId;
    }
}

package com.tericcabrel.parking.models.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tericcabrel.parking.models.dbs.Customer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class CustomerListResponse {
    private List<Customer> data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CustomerListResponse(@JsonProperty("data") List<Customer> data) {
        this.data = data;
    }
}

package com.tericcabrel.parking.models.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tericcabrel.parking.models.dbs.Customer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CustomerResponse {
    private Customer data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CustomerResponse(@JsonProperty("data") Customer data) {
        this.data = data;
    }
}

package com.tericcabrel.parking.models.dbs;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class Car {
    private String model;

    private String brand;

    private String numberPlate;

    @Builder
    public Car(String model, String brand, String numberPlate) {
        this.model = model;
        this.brand = brand;
        this.numberPlate = numberPlate;
    }
}

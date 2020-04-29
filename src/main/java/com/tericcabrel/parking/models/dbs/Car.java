package com.tericcabrel.parking.models.dbs;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.Indexed;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class Car {
    private String model;

    private String brand;

    @Indexed(unique = true)
    private String numberPlate;

    @Builder
    public Car(String model, String brand, String numberPlate) {
        this.model = model;
        this.brand = brand;
        this.numberPlate = numberPlate;
    }
}

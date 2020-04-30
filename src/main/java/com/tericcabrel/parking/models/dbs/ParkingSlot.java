package com.tericcabrel.parking.models.dbs;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.Date;

import com.tericcabrel.parking.models.enums.ParkingSlotStateEnum;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "parkingSlots")
public class ParkingSlot extends BaseModel {
    private String label;

    @Field(value = "state", targetType = FieldType.STRING)
    private ParkingSlotStateEnum state;

    private PricingPolicy pricingPolicy;

    @DBRef
    private CarType carType;

    public ParkingSlot() {
        this.state = ParkingSlotStateEnum.FREE;
    }

    @Builder
    public ParkingSlot(
        String id, Date createdAt, Date updatedAt, String label, ParkingSlotStateEnum state,
        PricingPolicy pricingPolicy, CarType carType
    ) {
        super(id, createdAt, updatedAt);

        this.label = label;
        this.state = state;
        this.pricingPolicy = pricingPolicy;
        this.carType = carType;
    }
}

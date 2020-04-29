package com.tericcabrel.parking.models.db;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.Date;

import com.tericcabrel.parking.models.enums.ParkingSlotStateEnum;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "parkingSlots")
public class ParkingSlot extends BaseModel {
    private String label;

    @Field(value = "state", targetType = FieldType.STRING)
    private ParkingSlotStateEnum state;

    private PricingPolicy pricingPolicy;

    @Builder
    public ParkingSlot(
        String id, Date createdAt, Date updatedAt, String label, ParkingSlotStateEnum state, PricingPolicy pricingPolicy
    ) {
        super(id, createdAt, updatedAt);

        this.label = label;
        this.state = state;
        this.pricingPolicy = pricingPolicy;
    }
}
package com.tericcabrel.parking.models.dbs;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "carRechargeSessions")
public class CarRechargeSession extends BaseModel {
    private Date startTime;

    private Date endTime;

    private double price;

    @DBRef
    private ParkingSlot parkingSlot;

    @DBRef
    private Customer customer;

    @Builder
    public CarRechargeSession(
        String id, Date createdAt, Date updatedAt, Date startTime, Date endTime, double price,
        ParkingSlot parkingSlot, Customer customer
    ) {
        super(id, createdAt, updatedAt);

        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.parkingSlot = parkingSlot;
        this.customer = customer;
    }

    /**
     * @return the difference in seconds between the end time and the start time
     */
    public long getDurationInSecond() {
        if (startTime != null && endTime != null) {
            return (endTime.getTime() - startTime.getTime()) / 1000;
        }

        return 0;
    }
}

package com.tericcabrel.parking.models.dbs;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "carsTypes")
public class CarType extends BaseModel {
    @Indexed
    private String name;

    @Builder
    public CarType(String id, Date createdAt, Date updatedAt, String name) {
        super(id, createdAt, updatedAt);

        this.name = name;
    }
}

package com.tericcabrel.parking.models.dbs;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "carTypes")
public class CarType extends BaseModel {
    private String name;

    @Builder
    public CarType(String id, Date createdAt, Date updatedAt, String name) {
        super(id, createdAt, updatedAt);

        this.name = name;
    }
}

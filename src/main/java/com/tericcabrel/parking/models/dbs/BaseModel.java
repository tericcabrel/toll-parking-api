package com.tericcabrel.parking.models.dbs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BaseModel {
    @MongoId(FieldType.OBJECT_ID)
    protected String id;

    protected Date createdAt;

    protected Date updatedAt;
}

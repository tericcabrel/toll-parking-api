package com.tericcabrel.parking.models.dbs;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.Date;

import com.tericcabrel.parking.models.enums.GenderEnum;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "customers")
public class Customer extends BaseModel {
    private String name;

    private String email;

    @Field(targetType = FieldType.STRING)
    private GenderEnum gender;

    private String phone;

    @DBRef
    private CarType carType;

    @Builder
    public Customer(
        String id, Date createdAt, Date updatedAt, String name, String email, GenderEnum gender,
        String phone, CarType carType
    ) {
        super(id, createdAt, updatedAt);

        this.name = name;
        this.email = email;
        this.gender = gender;
        this.phone = phone;
        this.carType = carType;
    }
}

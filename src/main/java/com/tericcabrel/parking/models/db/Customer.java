package com.tericcabrel.parking.models.db;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
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
    @Indexed
    private String name;

    @Indexed(unique = true)
    private String email;

    @Field(targetType = FieldType.STRING)
    private GenderEnum gender;

    private String phone;

    private Car car;

    public Customer(
        String id, Date createdAt, Date updatedAt, String name, String email, GenderEnum gender, String phone, Car car
    ) {
        super(id, createdAt, updatedAt);

        this.name = name;
        this.email = email;
        this.gender = gender;
        this.phone = phone;
        this.car = car;
    }
}
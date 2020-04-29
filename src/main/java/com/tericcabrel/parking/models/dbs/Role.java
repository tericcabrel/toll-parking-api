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
@Document(collection = "roles")
public class Role extends BaseModel {
    private String name;

    private String description;

    @Builder
    public Role(String id, Date createdAt, Date updatedAt, String name, String description) {
        super(id, createdAt, updatedAt);

        this.name = name;
        this.description = description;
    }
}

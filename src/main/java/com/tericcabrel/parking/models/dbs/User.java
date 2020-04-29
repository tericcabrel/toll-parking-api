package com.tericcabrel.parking.models.dbs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.tericcabrel.parking.models.enums.GenderEnum;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "users")
public class User extends BaseModel {
    private String name;

    @Indexed(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    @Field(value = "gender", targetType = FieldType.STRING)
    private GenderEnum gender;

    @DBRef
    private List<Role> roles;

    public User() {
        roles = new ArrayList<>();
    }

    @Builder
    public User(
        String id, Date createdAt, Date updatedAt, String name, String email, String password, GenderEnum gender
    ) {
        super(id, createdAt, updatedAt);

        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;

        roles = new ArrayList<>();
    }

    /**
     * @param role Role to add
     *
     * @return New list of role
     */
    public User addRole(Role role) {
        this.roles.add(role);

        return this;
    }

    /**
     * @param roleName Name of the role
     *
     * @return <code>true</code> if the user has the role otherwise <code>false</code>
     */
    public boolean hasRole(String roleName) {
        return this.roles.stream().anyMatch(role -> role.getName().equals(roleName));
    }

    /**
     * @param role Role to delete
     *
     * @return New list of role without the one passed in parameters
     */
    public User removeRole(Role role) {
        Stream<Role> newRoles = this.roles.stream().filter(role1 -> !role1.getName().equals(role.getName()));

        this.roles = newRoles.collect(Collectors.toList());

        return this;
    }
}

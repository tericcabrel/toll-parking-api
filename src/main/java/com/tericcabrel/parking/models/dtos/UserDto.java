package com.tericcabrel.parking.models.dtos;

import com.tericcabrel.parking.constraints.FieldMatch;
import com.tericcabrel.parking.models.dbs.Role;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.util.List;

@FieldMatch.List({
    @FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match")
})
@Data
@EqualsAndHashCode(callSuper = false)
public class UserDto extends BaseUserDto {
    private String id;

    @NotBlank(message = "The first name is required")
    private String name;

    @Email(message = "Email address is not valid")
    @NotBlank(message = "The email address is required")
    private String email;

    @Size(min = 6, message = "Must be at least 6 characters")
    @NotBlank(message = "The password is required")
    private String password;

    @NotBlank(message = "This field is required")
    private String confirmPassword;

    private boolean enabled;

    @NotEmpty(message = "At least one role's name is required")
    @NotNull(message = "This field is required")
    private String[] roleNames;

    private List<Role> roles;

    public UserDto() {
        enabled = true;
        roleNames = new String[]{ };
    }

    @Builder
    public UserDto(
        String name, String email, String password, String confirmPassword, boolean enabled, List<Role> roles
    ) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.enabled = enabled;
        this.roles = roles;
    }
}

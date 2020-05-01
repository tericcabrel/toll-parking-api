package com.tericcabrel.parking.models.dtos;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@ApiModel(value = "LoginUserParam", description = "Parameters required to login user")
@NoArgsConstructor
@Data
public class LoginUserDto {
    @ApiModelProperty(notes = "User email address", required = true)
    @Email(message = "Email address is not valid")
    @NotBlank(message = "The email address is required")
    private String email;

    @ApiModelProperty(notes = "User password (Min character: 6)", required = true)
    @Size(min = 6, message = "Must be at least 6 characters")
    private String password;

    @Builder
    public LoginUserDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

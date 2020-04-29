package com.tericcabrel.parking.models.dtos;

import com.tericcabrel.parking.constraints.FieldMatch;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldMatch.List({
    @FieldMatch(first = "newPassword", second = "confirmNewPassword", message = "The password fields must match")
})
@NoArgsConstructor
@Data
public class UpdatePasswordDto {
    @Size(min = 6, message = "Must be at least 6 characters")
    @NotBlank(message = "This field is required")
    private String currentPassword;

    @Size(min = 6, message = "Must be at least 6 characters")
    @NotBlank(message = "This field is required")
    private String newPassword;

    @Size(min = 6, message = "Must be at least 6 characters")
    @NotBlank(message = "This field is required")
    private String confirmNewPassword;

    @Builder
    public UpdatePasswordDto(String currentPassword, String newPassword, String confirmNewPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }
}

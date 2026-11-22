package com.deepan.orderpay.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "Current password is required")
        String currentPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 10, max = 72, message = "New password must be between 10 and 72 characters")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).*$",
                message = "New password must contain at least one letter and one digit")
        String newPassword) {

    @Override
    public String toString() {
        return "ChangePasswordRequest[currentPassword=***, newPassword=***]";
    }
}

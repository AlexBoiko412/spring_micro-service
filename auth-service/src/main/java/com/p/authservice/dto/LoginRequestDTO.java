package com.p.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequestDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Not valid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Minimal length must be 8 symbols")
    private String password;

    public @NotBlank(message = "Email is required") @Email(message = "Not valid email") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email is required") @Email(message = "Not valid email") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Password is required") @Size(min = 8, message = "Minimal length must be 8 symbols") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password is required") @Size(min = 8, message = "Minimal length must be 8 symbols") String password) {
        this.password = password;
    }
}

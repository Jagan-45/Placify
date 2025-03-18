package com.murali.placify.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDTO {
    @NotBlank
    private String username;
    @NotBlank
    @Email(message = "Enter valid email address")
    private String mailID;
    @NotBlank
    @Size(min = 8, max = 16, message = "Enter valid password length")
    private String password;
}

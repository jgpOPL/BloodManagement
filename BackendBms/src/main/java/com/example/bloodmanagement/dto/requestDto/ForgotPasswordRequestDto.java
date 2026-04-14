package com.example.bloodmanagement.dto.requestDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequestDto {

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    private String email;
}

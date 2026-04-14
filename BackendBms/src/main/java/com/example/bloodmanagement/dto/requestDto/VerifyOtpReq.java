package com.example.bloodmanagement.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyOtpReq {
    @NotBlank(message = "Username should not be blank,empty or null")
    private String username;
    @NotBlank(message = "Username should not be blank,empty or null")
    private String otp;
    @Size(min = 8,message = "Minimum size should be 8")
    private String password;
}

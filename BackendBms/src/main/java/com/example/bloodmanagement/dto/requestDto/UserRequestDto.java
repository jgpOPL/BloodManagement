package com.example.bloodmanagement.dto.requestDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    @NotBlank
    private String username;
    @Email(message = "Invalid Email")
    private String email;
    @Size(min = 8,message = "Minimum size should be 8")
    private String password;
    private String role;
}

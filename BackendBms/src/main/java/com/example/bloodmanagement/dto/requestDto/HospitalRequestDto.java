package com.example.bloodmanagement.dto.requestDto;

import com.example.bloodmanagement.dto.responseDto.UserResponseDto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalRequestDto {
    private String hospitalName;
    @Size(min = 5,message = "Minimum address size should be 5")
    private String address;
    @Pattern(regexp = "^[1-9][0-9]{9}$",message = "Phone number must be 10 digits and should not start with 0")
    private String contactNo;
    @NotBlank(message = "LicenseNo should not be blank, empty or null")
    private String licenseNo;

    private Long userId ;

}

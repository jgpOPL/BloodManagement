package com.example.bloodmanagement.dto.requestDto;

import com.example.bloodmanagement.enums.BloodGroup;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonorRequestDto {
    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;
    @NotBlank(message = "Name should not be left Blank or null")
    private String name;
    @Min(value = 18,message = "Donor's age should be atleast 18")
    @Max(value = 50,message = "Donor's age should be less than 50")
    private Integer age;
    @NotBlank(message = "Name should not be left Blank or null")
    private String gender;
    @Min(value = 40, message = "Weight should be atleast 40")
    private double weight;
    private double height;
    @NotBlank(message = "city should not be left Blank or null")
    private String city;
    private Long userId;
}

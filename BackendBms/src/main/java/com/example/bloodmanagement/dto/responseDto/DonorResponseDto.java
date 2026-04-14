package com.example.bloodmanagement.dto.responseDto;

import com.example.bloodmanagement.enums.BloodGroup;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonorResponseDto {
    private Long donorId;
    private String name;
    private BloodGroup bloodGroup;
    private Integer age;
    private double weight;
    private double height;
    private String gender;
    private String city;
    private LocalDate lastDonationDate;
    private Long userId;
    private List<Long> donationId;
    private String status;
}

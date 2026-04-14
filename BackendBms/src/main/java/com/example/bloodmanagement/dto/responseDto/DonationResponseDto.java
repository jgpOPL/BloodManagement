package com.example.bloodmanagement.dto.responseDto;

import com.example.bloodmanagement.enums.BloodGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonationResponseDto {
    private Long donationId;
    private LocalDate donationDate;
    private BloodGroup bloodGroup;
    private double quantity;
    private String remarks;
    private Long donorId;
}

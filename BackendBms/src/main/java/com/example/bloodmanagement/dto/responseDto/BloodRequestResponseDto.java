package com.example.bloodmanagement.dto.responseDto;

import com.example.bloodmanagement.enums.BloodGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BloodRequestResponseDto {

    private Long bloodRequestId;
    private BloodGroup bloodGroup;
    private double quantity;
    private String status;
    private LocalDateTime requestDate;
    private String hospitalName;
    private Long hospitalId;
}

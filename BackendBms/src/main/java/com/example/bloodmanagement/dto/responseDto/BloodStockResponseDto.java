package com.example.bloodmanagement.dto.responseDto;

import com.example.bloodmanagement.enums.BloodGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BloodStockResponseDto {
    private Long bloodStockId;
    private BloodGroup bloodGroup;
    private double units;
    private LocalDateTime lastUpdated;
}

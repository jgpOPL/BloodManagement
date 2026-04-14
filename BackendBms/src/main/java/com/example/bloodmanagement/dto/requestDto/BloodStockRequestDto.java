package com.example.bloodmanagement.dto.requestDto;

import com.example.bloodmanagement.enums.BloodGroup;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BloodStockRequestDto {

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;
    private double units;

}

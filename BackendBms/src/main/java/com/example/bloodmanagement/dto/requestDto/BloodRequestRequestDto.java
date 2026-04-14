package com.example.bloodmanagement.dto.requestDto;

import com.example.bloodmanagement.enums.BloodGroup;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BloodRequestRequestDto {

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;
    private double quantity;
    private Long hospitalId;
}

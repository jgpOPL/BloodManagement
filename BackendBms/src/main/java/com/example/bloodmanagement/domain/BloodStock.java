package com.example.bloodmanagement.domain;

import com.example.bloodmanagement.enums.BloodGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BloodStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bloodStockId;
    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;
    private double units;
    private LocalDateTime lastUpdated;
}

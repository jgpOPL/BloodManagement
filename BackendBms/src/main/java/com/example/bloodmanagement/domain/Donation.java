package com.example.bloodmanagement.domain;

import com.example.bloodmanagement.dto.requestDto.BloodRequestRequestDto;
import com.example.bloodmanagement.enums.BloodGroup;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Donation_id")
    private Long donationId;
    private LocalDate donationDate;
    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;
    private double quantity;
    private String remarks;

    @ManyToOne
    @JoinColumn(name = "donor_id")
    private Donor donor;

}

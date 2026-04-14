package com.example.bloodmanagement.domain;

import com.example.bloodmanagement.enums.BloodGroup;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

    @Entity
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class BloodRequest {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "bloodRequest_Id")
        private Long bloodRequestId;
        @Enumerated(EnumType.STRING)
        private BloodGroup bloodGroup;
        private double quantity;
        private String status;
        private String hospitalName;
        private LocalDateTime requestDate;

        @ManyToOne
        @JoinColumn(name = "hospital_id")
        private Hospital hospital;
    }

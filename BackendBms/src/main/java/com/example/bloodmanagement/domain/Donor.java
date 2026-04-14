package com.example.bloodmanagement.domain;

import com.example.bloodmanagement.enums.BloodGroup;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

    @Entity
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Donor {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "donor_id")
        private Long donorId;
        private String name;
        private Integer age;
        private double weight;
        private double height;
        @Enumerated(EnumType.STRING)
        private BloodGroup bloodGroup;
        private String gender;
        private String city;
        private LocalDate lastDonationDate;
        private String status;

        @OneToOne(cascade = CascadeType.ALL)
        @JoinColumn(name = "user_id", unique = true)
        private Users user;

        @OneToMany(mappedBy = "donor", cascade = CascadeType.ALL)
        private List<Donation> donation;
    }

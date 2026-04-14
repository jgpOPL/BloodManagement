package com.example.bloodmanagement.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Hospital_id")
    private Long hospitalId;
    private String hospitalName;
    private String address;
    private String contactNo;
    private String licenseNo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "User_id")
    private Users user;

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL)
    private List<BloodRequest> bloodRequests;
}

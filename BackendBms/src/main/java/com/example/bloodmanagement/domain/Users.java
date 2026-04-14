package com.example.bloodmanagement.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "User_id")
    private Long id;
    private String username;
    private String email;
    private String password;
    private String role;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int failedAttempts = 0;

    @Column(nullable = true)
    private LocalDateTime lockedUntil;
}

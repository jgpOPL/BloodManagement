package com.example.bloodmanagement.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private String token;
    private String role;
    private String username;
    // lock info — only populated on failed attempts
    private boolean locked;
    private long lockedUntilEpochMs; // 0 if not locked
    private int failedAttempts;
}

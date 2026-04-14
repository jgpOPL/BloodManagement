package com.example.bloodmanagement.controller;

import com.example.bloodmanagement.dto.requestDto.ForgotPasswordRequestDto;
import com.example.bloodmanagement.dto.requestDto.LoginRequestDto;
import com.example.bloodmanagement.dto.requestDto.ResetPasswordRequestDto;
import com.example.bloodmanagement.dto.requestDto.UserRequestDto;
import com.example.bloodmanagement.dto.requestDto.VerifyOtpReq;
import com.example.bloodmanagement.dto.responseDto.LoginResponseDto;
import com.example.bloodmanagement.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public String registerUser(@Valid @RequestBody UserRequestDto dto) {
        return authService.registerUser(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        return new ResponseEntity<>(authService.login(dto), HttpStatus.OK);
    }

    // ── Legacy OTP endpoints ──────────────────────────────────────────────────

    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgetPassword(@Valid @RequestBody LoginRequestDto dto) {
        return new ResponseEntity<>(authService.forgotPassword(dto), HttpStatus.OK);
    }

    @PostMapping("/VerifyOtp")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody VerifyOtpReq req) {
        return new ResponseEntity<>(authService.verifyOtp(req), HttpStatus.OK);
    }

    // ── Email link-based password reset ──────────────────────────────────────

    @PostMapping("/forgot-password")
    public ResponseEntity<String> sendResetLink(@Valid @RequestBody ForgotPasswordRequestDto dto) {
        return new ResponseEntity<>(authService.sendPasswordResetLink(dto), HttpStatus.OK);
    }

    @GetMapping("/verify-reset-token")
    public ResponseEntity<String> verifyResetToken(@RequestParam String token) {
        return new ResponseEntity<>(authService.verifyResetToken(token), HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDto dto) {
        return new ResponseEntity<>(authService.resetPassword(dto), HttpStatus.OK);
    }
}

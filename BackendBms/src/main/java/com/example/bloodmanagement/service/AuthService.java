package com.example.bloodmanagement.service;

import com.example.bloodmanagement.dto.requestDto.ForgotPasswordRequestDto;
import com.example.bloodmanagement.dto.requestDto.LoginRequestDto;
import com.example.bloodmanagement.dto.requestDto.ResetPasswordRequestDto;
import com.example.bloodmanagement.dto.requestDto.UserRequestDto;
import com.example.bloodmanagement.dto.requestDto.VerifyOtpReq;
import com.example.bloodmanagement.dto.responseDto.LoginResponseDto;

public interface AuthService {
    String registerUser(UserRequestDto dto);
    LoginResponseDto login(LoginRequestDto dto);
    // Legacy OTP methods (kept for backward compatibility)
    String forgotPassword(LoginRequestDto l);
    String verifyOtp(VerifyOtpReq verifyOtpReq);
    // Email link-based password reset
    String sendPasswordResetLink(ForgotPasswordRequestDto dto);
    String verifyResetToken(String token);
    String resetPassword(ResetPasswordRequestDto dto);
}

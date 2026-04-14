package com.example.bloodmanagement.service;

import com.example.bloodmanagement.dto.requestDto.BloodRequestRequestDto;
import com.example.bloodmanagement.dto.requestDto.HospitalRequestDto;
import com.example.bloodmanagement.dto.responseDto.HospitalResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface HospitalService {
    String registerHospital(HospitalRequestDto dto);
    String requestBlood(BloodRequestRequestDto dto,HttpServletRequest req);
    HospitalResponseDto getHospital(Long id, HttpServletRequest req);
    String updateHospital(Long id,HospitalResponseDto dto);
}

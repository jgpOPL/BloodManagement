package com.example.bloodmanagement.service;

import com.example.bloodmanagement.dto.requestDto.DonationRequestDto;
import com.example.bloodmanagement.dto.requestDto.DonorRequestDto;
import com.example.bloodmanagement.dto.responseDto.DonationResponseDto;
import com.example.bloodmanagement.dto.responseDto.DonorResponseDto;
import com.example.bloodmanagement.dto.responseDto.PagedResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface DonorService {
    String saveDonor(DonorRequestDto dto);
    String donateBlood(DonationRequestDto donation, HttpServletRequest req);
    String updateDonor(Long id, DonorRequestDto dto);
    DonorResponseDto donorDetails(Long id, HttpServletRequest req);
    PagedResponse<DonationResponseDto> donorHistory(Long id, HttpServletRequest req, int page, int size);
    Long getUserId(String username);
}

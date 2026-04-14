package com.example.bloodmanagement.service;

import com.example.bloodmanagement.dto.requestDto.BloodStockRequestDto;
import com.example.bloodmanagement.dto.responseDto.DashboardSummaryDto;
import com.example.bloodmanagement.dto.responseDto.*;

import java.util.List;

public interface AdminService {
    String checkDonorEligibility(Long id);
    PagedResponse<UserResponseDto> getAllUsers(int page, int size);
    String addBloodStock(BloodStockRequestDto dto);
    String approveBloodRequest(Long id);
    PagedResponse<BloodRequestResponseDto> getBloodRequest(int page, int size);
    PagedResponse<BloodStockResponseDto> getBloodStock(int page, int size);
    PagedResponse<DonorResponseDto> getAllDonors(int page, int size);
    byte[] downloadExcel();
    String deleteUser(Long id);
    Long getUserId(String username);
    PagedResponse<DonationResponseDto> getAllDonations(int page, int size);
    DashboardSummaryDto getSummary();
}

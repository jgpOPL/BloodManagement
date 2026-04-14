package com.example.bloodmanagement.service.implementation;

import com.example.bloodmanagement.custom.NoUserFoundException;
import com.example.bloodmanagement.domain.*;
import com.example.bloodmanagement.dto.requestDto.BloodStockRequestDto;
import com.example.bloodmanagement.dto.responseDto.*;
import com.example.bloodmanagement.dto.responseDto.DashboardSummaryDto;
import com.example.bloodmanagement.repository.*;
import com.example.bloodmanagement.service.AdminService;
import com.example.bloodmanagement.utitity.MapperUtility;
import com.example.bloodmanagement.utitity.StorageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired private DonorRepository donorRepo;
    @Autowired private MapperUtility mapper;
    @Autowired private UsersRepository userRepo;
    @Autowired private DonationRepository donationRepository;
    @Autowired private BloodStockRepository bloodStockRepository;
    @Autowired private HospitalRepository hospitalRepository;
    @Autowired private BloodRequestRepository bloodReqRepo;

    // Clamp page/size to safe defaults
    private Pageable toPageable(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = (size < 1 || size > 100) ? 10 : size;
        return PageRequest.of(safePage, safeSize);
    }

    private <T> PagedResponse<T> toPagedResponse(Page<T> pageResult) {
        return new PagedResponse<>(
                pageResult.getContent(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.getNumber(),
                pageResult.getSize()
        );
    }

    @Override
    public String checkDonorEligibility(Long id) {
        Donor donor = donorRepo.findById(id)
                .orElseThrow(() -> new NoUserFoundException("No Donor found", HttpStatus.NOT_FOUND.value()));
        double bmi = donor.getWeight() / (donor.getHeight() * donor.getHeight());
        donor.setStatus(bmi > 18 ? "Approved" : "Rejected");
        donorRepo.save(donor);
        return "Donor eligibility checked";
    }

    @Override
    public PagedResponse<UserResponseDto> getAllUsers(int page, int size) {
        Page<Users> result = userRepo.findAll(toPageable(page, size));
        if (result.isEmpty()) throw new NoUserFoundException("No users found", HttpStatus.NOT_FOUND.value());
        return toPagedResponse(result.map(mapper::entityToDtoUser));
    }

    @Override
    public String addBloodStock(BloodStockRequestDto dto) {
        BloodStock stock = bloodStockRepository.findByBloodGroup(dto.getBloodGroup()).orElse(new BloodStock());
        stock.setBloodGroup(dto.getBloodGroup());
        stock.setUnits(stock.getUnits() + dto.getUnits());
        stock.setLastUpdated(LocalDateTime.now());
        bloodStockRepository.save(stock);
        return "Blood stock updated successfully";
    }

    @Override
    public String approveBloodRequest(Long id) {
        BloodRequest bloodRequest = bloodReqRepo.findById(id)
                .orElseThrow(() -> new NoUserFoundException("No Blood Request found", HttpStatus.NOT_FOUND.value()));
        if (bloodRequest.getStatus().equals("Pending")) {
            BloodStock bloodStock = bloodStockRepository.findByBloodGroup(bloodRequest.getBloodGroup())
                    .orElseGet(() -> {
                        bloodRequest.setStatus("Rejected");
                        bloodReqRepo.save(bloodRequest);
                        throw new NoUserFoundException("No blood group found", HttpStatus.NOT_FOUND.value());
                    });
            if (bloodRequest.getQuantity() <= bloodStock.getUnits()) {
                bloodRequest.setStatus("Approved");
                bloodStock.setUnits(bloodStock.getUnits() - bloodRequest.getQuantity());
                bloodStockRepository.save(bloodStock);
            } else {
                bloodRequest.setStatus("Rejected");
            }
        }
        bloodReqRepo.save(bloodRequest);
        return "Blood Request status checked";
    }

    @Override
    public PagedResponse<BloodRequestResponseDto> getBloodRequest(int page, int size) {
        Page<BloodRequest> result = bloodReqRepo.findByPending("Pending", toPageable(page, size));
        return toPagedResponse(result.map(mapper::entityToDtoBloodRequest));
    }

    @Override
    public PagedResponse<BloodStockResponseDto> getBloodStock(int page, int size) {
        Page<BloodStock> result = bloodStockRepository.findAll(toPageable(page, size));
        return toPagedResponse(result.map(mapper::entityToDtoBloodStock));
    }

    @Override
    public PagedResponse<DonorResponseDto> getAllDonors(int page, int size) {
        Page<Donor> result = donorRepo.findByPending("Pending", toPageable(page, size));
        return toPagedResponse(result.map(mapper::entityToDtoDonor));
    }

    @Override
    public byte[] downloadExcel() {
        return StorageHelper.getExcelDataFromDonations(
                donationRepository.findAll(), userRepo.findAll(), bloodStockRepository.findAll());
    }

    @Override
    public String deleteUser(Long id) {
        Users user = userRepo.findById(id)
                .orElseThrow(() -> new NoUserFoundException("No user found", HttpStatus.NOT_FOUND.value()));
        userRepo.delete(user);
        return "User deleted successfully";
    }

    @Override
    public Long getUserId(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new NoUserFoundException("No user found", HttpStatus.NOT_FOUND.value()))
                .getId();
    }

    @Override
    public DashboardSummaryDto getSummary() {
        return new DashboardSummaryDto(
            userRepo.count(),
            bloodReqRepo.count(),
            bloodReqRepo.countByStatus("Pending"),
            bloodReqRepo.countByStatus("Approved"),
            donorRepo.countByStatus("Pending"),
            donorRepo.countByStatus("Approved")
        );
    }

    @Override
    public PagedResponse<DonationResponseDto> getAllDonations(int page, int size) {
        Page<Donation> result = donationRepository.findAll(
                PageRequest.of(Math.max(page, 0), (size < 1 || size > 100) ? 10 : size,
                        Sort.by(Sort.Direction.DESC, "donationDate")));
        return toPagedResponse(result.map(mapper::entityToDtoDonation));
    }
}

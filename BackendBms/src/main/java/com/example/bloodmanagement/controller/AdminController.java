package com.example.bloodmanagement.controller;

import com.example.bloodmanagement.dto.requestDto.BloodStockRequestDto;
import com.example.bloodmanagement.dto.responseDto.*;
import com.example.bloodmanagement.dto.responseDto.DashboardSummaryDto;
import com.example.bloodmanagement.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4201")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/donor/approve/{id}")
    public String checkDonorEligibility(@PathVariable Long id) {
        return adminService.checkDonorEligibility(id);
    }

    @PostMapping("/blood-stock/add")
    public ResponseEntity<String> addBloodStock(@Valid @RequestBody BloodStockRequestDto dto) {
        return new ResponseEntity<>(adminService.addBloodStock(dto), HttpStatus.CREATED);
    }

    @GetMapping("/approve-bloodRequest/{id}")
    public ResponseEntity<String> approveBloodRequest(@PathVariable Long id) {
        return new ResponseEntity<>(adminService.approveBloodRequest(id), HttpStatus.OK);
    }

    @GetMapping("/allUsers")
    public ResponseEntity<PagedResponse<UserResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(adminService.getAllUsers(page, size), HttpStatus.OK);
    }

    @GetMapping("/allDonations")
    public ResponseEntity<PagedResponse<DonationResponseDto>> getAllDonations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(adminService.getAllDonations(page, size), HttpStatus.OK);
    }

    @GetMapping(value = "/report/")
    public ResponseEntity<byte[]> downloadExcelData() {
        String filename = "Blood_Management" + UUID.randomUUID() + ".xlsx";
        byte[] bytes = adminService.downloadExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }

    @GetMapping("/getBloodRequests")
    public ResponseEntity<PagedResponse<BloodRequestResponseDto>> getAllBloodRequest(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(adminService.getBloodRequest(page, size), HttpStatus.OK);
    }

    @GetMapping("/getBloodStock")
    public ResponseEntity<PagedResponse<BloodStockResponseDto>> getBloodStock(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(adminService.getBloodStock(page, size), HttpStatus.OK);
    }

    @GetMapping("/getAllDonors")
    public ResponseEntity<PagedResponse<DonorResponseDto>> getAllDonors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(adminService.getAllDonors(page, size), HttpStatus.OK);
    }

    @DeleteMapping("/removeUser/{id}")
    public ResponseEntity<String> removeUser(@PathVariable Long id) {
        return new ResponseEntity<>(adminService.deleteUser(id), HttpStatus.OK);
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto> getSummary() {
        return new ResponseEntity<>(adminService.getSummary(), HttpStatus.OK);
    }

    @GetMapping("/getUserId/{username}")
    public ResponseEntity<Long> getUserId(@PathVariable String username) {
        return new ResponseEntity<>(adminService.getUserId(username), HttpStatus.OK);
    }
}

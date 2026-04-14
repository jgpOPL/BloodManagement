package com.example.bloodmanagement.controller;

import com.example.bloodmanagement.dto.requestDto.DonationRequestDto;
import com.example.bloodmanagement.dto.requestDto.DonorRequestDto;

import com.example.bloodmanagement.dto.responseDto.DonationResponseDto;
import com.example.bloodmanagement.dto.responseDto.DonorResponseDto;
import com.example.bloodmanagement.dto.responseDto.PagedResponse;
import com.example.bloodmanagement.service.DonorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/donor")
public class DonorController {
    @Autowired
    private DonorService donorService;

    @PostMapping("/register")
    public ResponseEntity<String> registerDonor(@Valid @RequestBody DonorRequestDto dto)
    {
        return new ResponseEntity<>(donorService.saveDonor(dto), HttpStatus.CREATED);
    }
    @PostMapping("/donateBlood")
    public ResponseEntity<String> donateBlood(@Valid @RequestBody DonationRequestDto dto, HttpServletRequest req)
    {
        return new ResponseEntity<>(donorService.donateBlood(dto,req),HttpStatus.OK);
    }
    @GetMapping("/history/{id}")
    public ResponseEntity<PagedResponse<DonationResponseDto>> history(
            @PathVariable Long id,
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(donorService.donorHistory(id, req, page, size), HttpStatus.OK);
    }
    @GetMapping("/profile/{id}")
    public ResponseEntity<DonorResponseDto> donorProfile(@PathVariable Long id,HttpServletRequest req)
    {
        return new ResponseEntity<>(donorService.donorDetails(id,req),HttpStatus.OK);
    }
    @PutMapping("/profile/{id}")
    public ResponseEntity<String> donateBlood(@PathVariable Long id,@Valid @RequestBody DonorRequestDto dto)
    {
        return new ResponseEntity<>(donorService.updateDonor(id,dto),HttpStatus.OK);
    }
    @GetMapping("/getUserId/{username}")
    public ResponseEntity<Long> getUserId(@PathVariable String username)
    {
        return new ResponseEntity<>(donorService.getUserId(username),HttpStatus.OK);
    }
   
}

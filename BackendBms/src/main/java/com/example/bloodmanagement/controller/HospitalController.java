package com.example.bloodmanagement.controller;

import com.example.bloodmanagement.dto.requestDto.BloodRequestRequestDto;
import com.example.bloodmanagement.dto.requestDto.DonorRequestDto;
import com.example.bloodmanagement.dto.requestDto.HospitalRequestDto;
import com.example.bloodmanagement.dto.responseDto.HospitalResponseDto;
import com.example.bloodmanagement.service.HospitalService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hospital")
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;
    @PostMapping("/register")
    public ResponseEntity<String> registerHospital(@Valid@RequestBody HospitalRequestDto dto)
    {
        return new ResponseEntity<>(hospitalService.registerHospital(dto), HttpStatus.CREATED);
    }
    @PostMapping("/request")
    public ResponseEntity<String> requestBlood(@Valid @RequestBody BloodRequestRequestDto dto,HttpServletRequest req)
    {
        return new ResponseEntity<>(hospitalService.requestBlood(dto,req),HttpStatus.OK);
    }
    @GetMapping("/profile/{id}")
    public ResponseEntity<HospitalResponseDto> requestBlood(@PathVariable Long id, HttpServletRequest req)
    {
        return new ResponseEntity<>( hospitalService.getHospital(id,req),HttpStatus.OK);
    }
    @PutMapping("update/{id}")
    public ResponseEntity<String>  updateHospital(@PathVariable Long id,@RequestBody HospitalResponseDto dto)
    {
        return new ResponseEntity<>(hospitalService.updateHospital(id,dto),HttpStatus.OK);
    }

}

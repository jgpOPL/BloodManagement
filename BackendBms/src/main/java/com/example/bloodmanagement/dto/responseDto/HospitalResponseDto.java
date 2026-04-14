package com.example.bloodmanagement.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalResponseDto {
    private Long hospitalId;
    private String hospitalName;
    private String address;
    private String contactNo;
    private String licenseNo;

    private Long userId;
    private List<Long> bloodRequestId ;
}

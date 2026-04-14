package com.example.bloodmanagement.utitity;

import com.example.bloodmanagement.domain.*;
import com.example.bloodmanagement.dto.requestDto.*;
import com.example.bloodmanagement.dto.responseDto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class MapperUtility {
    @Autowired
    private ObjectMapper mapper;

    public BloodRequest dtoToEntityBloodRequest(BloodRequestRequestDto bloodReqDto)
    {
        return mapper.convertValue(bloodReqDto,BloodRequest.class);
    }
    public BloodRequestResponseDto entityToDtoBloodRequest(BloodRequest bloodReq)
    {
        BloodRequestResponseDto bloodRequestRequestDto = new BloodRequestResponseDto();
        bloodRequestRequestDto.setBloodGroup(bloodReq.getBloodGroup());
        bloodRequestRequestDto.setQuantity(bloodReq.getQuantity());
        bloodRequestRequestDto.setStatus(bloodReq.getStatus());
        bloodRequestRequestDto.setRequestDate(bloodReq.getRequestDate());
        bloodRequestRequestDto.setBloodRequestId(bloodReq.getBloodRequestId());
        bloodRequestRequestDto.setHospitalName(bloodReq.getHospitalName());
        bloodRequestRequestDto.setHospitalId(bloodReq.getHospital().getHospitalId());
        return bloodRequestRequestDto;
    }


    public BloodStock dtoToEntityBloodStock(BloodStockRequestDto bloodStockDto)
    {
        return mapper.convertValue(bloodStockDto,BloodStock.class);
    }
    public BloodStockResponseDto entityToDtoBloodStock(BloodStock bloodStock)
    {
        return mapper.convertValue(bloodStock,BloodStockResponseDto.class);
    }


    public Donation dtoToEntityDonation(DonationRequestDto donationDto)
    {
        return mapper.convertValue(donationDto,Donation.class);
    }
    public DonationResponseDto entityToDtoDonation(Donation donation)
    {
//        donation.getDonor().setDonation(null);
        DonationResponseDto donationResponseDto = new DonationResponseDto();
        donationResponseDto.setRemarks(donation.getRemarks());
        donationResponseDto.setQuantity(donation.getQuantity());
        donationResponseDto.setBloodGroup(donation.getBloodGroup());
        donationResponseDto.setDonationDate(donation.getDonationDate());
        donationResponseDto.setDonorId(donation.getDonor().getDonorId());
        donationResponseDto.setDonationId(donation.getDonationId());
        return donationResponseDto;
    }


    public Users dtoToEntityUser(UserRequestDto userDto)
    {
        return mapper.convertValue(userDto,Users.class);
    }
    public UserResponseDto entityToDtoUser(Users user)
    {
        return mapper.convertValue(user,UserResponseDto.class);
    }


    public Donor dtoToEntityDonor(DonorRequestDto donorDto)
    {
        return mapper.convertValue(donorDto,Donor.class);
    }
    public DonorResponseDto entityToDtoDonor(Donor donor)
    {
//        if (donor.getDonation() != null)donor.getDonation().forEach(d->d.setDonor(null));

        DonorResponseDto donorResponseDto = new DonorResponseDto();
        donorResponseDto.setBloodGroup(donor.getBloodGroup());
        donorResponseDto.setAge(donor.getAge());
        donorResponseDto.setCity(donor.getCity());
        donorResponseDto.setGender(donor.getGender());
        donorResponseDto.setHeight(donor.getHeight());
        donorResponseDto.setWeight(donor.getWeight());
        donorResponseDto.setLastDonationDate(donor.getLastDonationDate());
        donorResponseDto.setName(donor.getName());
        donorResponseDto.setDonorId(donor.getDonorId());
        donorResponseDto.setStatus(donor.getStatus());
        donorResponseDto.setUserId(donor.getUser().getId());
        donorResponseDto.setDonationId(donor.getDonation().stream().map(d->d.getDonationId()).toList());
        return  donorResponseDto;
    }

    public Hospital dtoToEntityHospital(HospitalRequestDto hospitalDto)
    {
        return mapper.convertValue(hospitalDto,Hospital.class);
    }
    public HospitalResponseDto entityToDtoHospital(Hospital hospital)
    {
        HospitalResponseDto hospitalResponseDto = new HospitalResponseDto();
        hospitalResponseDto.setHospitalName(hospital.getHospitalName());
        hospitalResponseDto.setAddress(hospital.getAddress());
        hospitalResponseDto.setContactNo(hospital.getContactNo());
        hospitalResponseDto.setLicenseNo(hospital.getLicenseNo());
        hospitalResponseDto.setHospitalId(hospital.getHospitalId());
        hospitalResponseDto.setUserId(hospital.getUser().getId());
        hospitalResponseDto.setBloodRequestId(hospital.getBloodRequests().stream().map(b->b.getBloodRequestId()).toList());
        return hospitalResponseDto;
    }


}

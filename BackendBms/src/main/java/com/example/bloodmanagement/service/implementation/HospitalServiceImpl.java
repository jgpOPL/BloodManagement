package com.example.bloodmanagement.service.implementation;

import com.example.bloodmanagement.custom.NoUserFoundException;
import com.example.bloodmanagement.domain.BloodRequest;
import com.example.bloodmanagement.domain.Hospital;
import com.example.bloodmanagement.domain.Users;
import com.example.bloodmanagement.dto.requestDto.BloodRequestRequestDto;
import com.example.bloodmanagement.dto.requestDto.HospitalRequestDto;
import com.example.bloodmanagement.dto.responseDto.HospitalResponseDto;
import com.example.bloodmanagement.repository.BloodRequestRepository;
import com.example.bloodmanagement.repository.HospitalRepository;
import com.example.bloodmanagement.repository.UsersRepository;
import com.example.bloodmanagement.service.HospitalService;
import com.example.bloodmanagement.utitity.JWTUtil;
import com.example.bloodmanagement.utitity.MapperUtility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private MapperUtility mapper;
    @Autowired
    private UsersRepository userRepo;
    @Autowired
    private HospitalRepository hospitalRepo;
    @Autowired
    private BloodRequestRepository bloodRequestRepo;
    @Autowired
    private JWTUtil jwtUtil;
    @Override
    public String registerHospital(HospitalRequestDto dto) {
        Optional<Users> byId = userRepo.findById(dto.getUserId());
        if(byId.isEmpty()) throw new NoUserFoundException("No hospital sign up found", HttpStatus.NOT_FOUND.value());
        Users users = byId.get();
        Hospital hospital = mapper.dtoToEntityHospital(dto);
        hospital.setUser(users);
        hospitalRepo.save(hospital);
        return "Hospital registered successfully";
    }

    @Override
    public String requestBlood(BloodRequestRequestDto dto,HttpServletRequest req) {

        String authToken = req.getHeader("Authorization");
        String token = authToken.substring(7);
        String username = jwtUtil.extractUsername(token);
        Optional<Users> byUsername = userRepo.findByUsername(username);
        Users users = byUsername.get();
        Long id = users.getId();

//        Optional<Hospital> byId = hospitalRepo.findById(dto.getHospitalId());
        Optional<Hospital> byId = hospitalRepo.findByUserId(id);
        if(byId.isEmpty())throw new NoUserFoundException("No hospital sign up found", HttpStatus.NOT_FOUND.value());
        Hospital hospital = byId.get();
        BloodRequest bloodRequest = mapper.dtoToEntityBloodRequest(dto);
        bloodRequest.setRequestDate(LocalDateTime.now());
        bloodRequest.setHospital(hospital);
        bloodRequest.setStatus("Pending");
        bloodRequest.setHospitalName(hospital.getHospitalName());
        bloodRequestRepo.save(bloodRequest);
        return "Blood Request is completed";
    }

    @Override
    public HospitalResponseDto getHospital(Long id, HttpServletRequest req) {
        String authToken = req.getHeader("Authorization");
        String token = authToken.substring(7);
        String username = jwtUtil.extractUsername(token);
        Optional<Hospital> byId = hospitalRepo.findById(id);

        if(byId.isEmpty()) throw new NoUserFoundException("No hospital found", HttpStatus.NOT_FOUND.value());
        if(!username.equals(byId.get().getUser().getUsername()))
        {
            throw new NoUserFoundException("Hospital does not match with logged in hospital",HttpStatus.NOT_FOUND.value());
        }
        Hospital hospital = byId.get();
        return mapper.entityToDtoHospital(hospital);
    }

    @Override
    public String updateHospital(Long id,HospitalResponseDto dto) {
        Hospital hospital=hospitalRepo.findById(id).orElseThrow(() -> new  NoUserFoundException("donor not found", HttpStatus.NOT_FOUND.value()));
        if(dto.getHospitalName()!=null) hospital.setHospitalName(dto.getHospitalName());
        if(dto.getAddress()!=null) hospital.setAddress(dto.getAddress());
        if(dto.getLicenseNo()!=null) hospital.setLicenseNo(dto.getLicenseNo());
        if(dto.getContactNo()!=null) hospital.setContactNo(dto.getContactNo());
        if(dto.getUserId()!=null)
        {
            Users user=userRepo.findById(dto.getUserId()).orElseThrow(() -> new NoUserFoundException("User not found", HttpStatus.NOT_FOUND.value()));
            hospital.setUser(user);
        }
        hospitalRepo.save(hospital);
        return "Hospital updated successfully";
    }
}

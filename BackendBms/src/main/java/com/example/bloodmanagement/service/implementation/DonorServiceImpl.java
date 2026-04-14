package com.example.bloodmanagement.service.implementation;

import com.example.bloodmanagement.custom.NoUserFoundException;
import com.example.bloodmanagement.domain.BloodStock;
import com.example.bloodmanagement.domain.Donation;
import com.example.bloodmanagement.domain.Donor;
import com.example.bloodmanagement.domain.Users;
import com.example.bloodmanagement.dto.requestDto.DonationRequestDto;
import com.example.bloodmanagement.dto.requestDto.DonorRequestDto;
import com.example.bloodmanagement.dto.responseDto.DonationResponseDto;
import com.example.bloodmanagement.dto.responseDto.DonorResponseDto;
import com.example.bloodmanagement.dto.responseDto.PagedResponse;
import com.example.bloodmanagement.enums.BloodGroup;
import com.example.bloodmanagement.repository.BloodStockRepository;
import com.example.bloodmanagement.repository.DonationRepository;
import com.example.bloodmanagement.repository.DonorRepository;
import com.example.bloodmanagement.repository.UsersRepository;
import com.example.bloodmanagement.service.DonorService;
import com.example.bloodmanagement.utitity.JWTUtil;
import com.example.bloodmanagement.utitity.MapperUtility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DonorServiceImpl implements DonorService {

    @Autowired
    private DonorRepository donorRepo;
    @Autowired
    private MapperUtility mapper;
    @Autowired
    private UsersRepository userRepo;
    @Autowired
    private DonationRepository donationRepository;
    @Autowired
    private BloodStockRepository bloodStockRepository;
    @Autowired
    private JWTUtil jwtUtil;
    @Override
    public String saveDonor(DonorRequestDto dto) {
        // Prevent duplicate donor registration for the same user
        if (donorRepo.findByUserId(dto.getUserId()).isPresent()) {
            throw new NoUserFoundException("Donor already registered for this user", HttpStatus.CONFLICT.value());
        }
        Donor donor = mapper.dtoToEntityDonor(dto);
        Optional<Users> byId = userRepo.findById(dto.getUserId());
        donor.setStatus("Pending");
        donor.setUser(byId.get());
        donorRepo.save(donor);
        return "Donor saved successfully";
    }

    @Override
    public String donateBlood(DonationRequestDto donation, HttpServletRequest req) {

        String authToken = req.getHeader("Authorization");
        String token = authToken.substring(7);
        String username = jwtUtil.extractUsername(token);
        Optional<Users> byUsername = userRepo.findByUsername(username);
        Users users = byUsername.get();
        Long id = users.getId();

        Optional<Donor> byId = donorRepo.findByUserId(id);
        if (byId.isEmpty()) {
            throw new NoUserFoundException("Donor not Found",HttpStatus.NOT_FOUND.value());
        }
        System.out.println("BYID"+byId.get().getName());
        System.out.println("token name : "+username);
        if(!username.equals(byId.get().getUser().getUsername()))
        {
            throw new NoUserFoundException("Donor does not match with logged in donor",HttpStatus.NOT_FOUND.value());
        }
        Donation donation1 = mapper.dtoToEntityDonation(donation);
        Donor donor =byId.get();
        if(donor.getStatus().equals("Approved"))
        {

            donor.setLastDonationDate(LocalDate.now());
            donorRepo.save(donor);

            donation1.setDonationDate(LocalDate.now());
            donation1.setBloodGroup(donor.getBloodGroup());
            donation1.setDonor(donor);
            donationRepository.save(donation1);


            BloodStock stock = bloodStockRepository.findByBloodGroup(donor.getBloodGroup()).orElse(new BloodStock());
            stock.setLastUpdated(LocalDateTime.now());
            stock.setBloodGroup(donor.getBloodGroup());
            stock.setUnits(stock.getUnits() + donation.getQuantity());

            bloodStockRepository.save(stock);
            return "Thank you "+donor.getName()+" for donating blood";
        }
        else
        {
            return donor.getName()+" Not eligible to donate blood ";
        }
    }
    @Override
    public Long getUserId(String username)
    {
        Optional<Users> byUsername = userRepo.findByUsername(username);
        if(byUsername.isEmpty()) throw new NoUserFoundException("No user found", HttpStatus.NOT_FOUND.value());
        Users users = byUsername.get();
        Optional<Donor> byUserId = donorRepo.findByUserId(users.getId());
        if(byUserId.isEmpty())throw new NoUserFoundException("No user found", HttpStatus.NOT_FOUND.value());
        Donor donor = byUserId.get();
        return donor.getDonorId();
    }

    @Override
    public String updateDonor(Long id, DonorRequestDto dto) {

        Donor donor = donorRepo.findById(id).orElseThrow(() -> new  NoUserFoundException("donor not found", HttpStatus.NOT_FOUND.value()));
        if(dto.getAge()!=null) donor.setAge(dto.getAge());
        if(dto.getName()!=null) donor.setName(dto.getName());
        if(dto.getBloodGroup()!=null) donor.setBloodGroup(dto.getBloodGroup());
        if(dto.getCity()!=null) donor.setCity(dto.getCity());
        if(dto.getGender()!=null) donor.setGender(dto.getGender());
        if(dto.getHeight()!=0) donor.setHeight(dto.getHeight());
        if(dto.getWeight()!=0) donor.setWeight(dto.getWeight());

        if(dto.getUserId()!=null){
            Users user = userRepo.findById(dto.getUserId()).orElseThrow(() -> new NoUserFoundException("User not found", HttpStatus.NOT_FOUND.value()));
            donor.setUser(user);
        }
        donorRepo.save(donor);

        return "Donor updated successfully";
    }

    @Override
    public DonorResponseDto donorDetails(Long id,HttpServletRequest req) {
        String authToken = req.getHeader("Authorization");
        String token = authToken.substring(7);
        String username = jwtUtil.extractUsername(token);

        Optional<Donor> byId = donorRepo.findById(id);
        if (byId.isEmpty()) {
            throw new NoUserFoundException("Donor not Found",HttpStatus.NOT_FOUND.value());
        }
        if(!username.equals(byId.get().getUser().getUsername()))
        {
            throw new NoUserFoundException("Donor does not match with logged in donor",HttpStatus.NOT_FOUND.value());
        }
        return mapper.entityToDtoDonor(byId.get());

    }

    @Override
    public PagedResponse<DonationResponseDto> donorHistory(Long id, HttpServletRequest req, int page, int size) {
        String authToken = req.getHeader("Authorization");
        String token = authToken.substring(7);
        String username = jwtUtil.extractUsername(token);

        Donor donor = donorRepo.findById(id)
                .orElseThrow(() -> new NoUserFoundException("Donor not Found", HttpStatus.NOT_FOUND.value()));
        if (!username.equals(donor.getUser().getUsername()))
            throw new NoUserFoundException("Donor does not match with logged in donor", HttpStatus.NOT_FOUND.value());

        int safePage = Math.max(page, 0);
        int safeSize = (size < 1 || size > 100) ? 10 : size;
        Page<Donation> result = donationRepository.findByDonor_DonorId(id,
                PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "donationDate")));

        if (result.isEmpty()) throw new NoUserFoundException("No donations found", HttpStatus.NOT_FOUND.value());

        return new PagedResponse<>(
                result.getContent().stream().map(mapper::entityToDtoDonation).toList(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }




}

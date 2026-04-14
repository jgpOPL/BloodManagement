package com.example.bloodmanagement.repository;

import com.example.bloodmanagement.domain.Donation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByDonor_DonorId(Long id);
    Page<Donation> findByDonor_DonorId(Long id, Pageable pageable);
}

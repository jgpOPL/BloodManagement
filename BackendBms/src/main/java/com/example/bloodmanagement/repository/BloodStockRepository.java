package com.example.bloodmanagement.repository;

import com.example.bloodmanagement.domain.BloodStock;
import com.example.bloodmanagement.enums.BloodGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BloodStockRepository extends JpaRepository<BloodStock,Long> {

    Optional<BloodStock> findByBloodGroup(BloodGroup bloodGroup);
}

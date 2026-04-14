package com.example.bloodmanagement.repository;

import com.example.bloodmanagement.domain.Hospital;
import com.example.bloodmanagement.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital,Long> {
    Optional<Hospital> findByUserId(Long id);
}

package com.example.bloodmanagement.repository;

import com.example.bloodmanagement.domain.Donor;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {

    // Use LIMIT 1 to guard against any existing duplicate user_id rows in DB
    @Query(value = "SELECT * FROM donor WHERE user_id = :userId LIMIT 1", nativeQuery = true)
    Optional<Donor> findByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT * FROM bloodmanagement.donor order by (status=:statusValue) DESC;",
           countQuery = "SELECT count(*) FROM bloodmanagement.donor",
           nativeQuery = true)
    Page<Donor> findByPending(@Param("statusValue") String statusValue, Pageable pageable);

    @Query(value = "SELECT * FROM bloodmanagement.donor order by (status=:statusValue) DESC;", nativeQuery = true)
    List<Donor> findByPending(@Param("statusValue") String statusValue);

    long countByStatus(String status);
}


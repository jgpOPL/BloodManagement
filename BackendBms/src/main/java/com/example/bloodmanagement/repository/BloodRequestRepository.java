package com.example.bloodmanagement.repository;

import com.example.bloodmanagement.domain.BloodRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BloodRequestRepository extends JpaRepository<BloodRequest,Long> {
    @Query(value = "SELECT * FROM bloodmanagement.blood_request order by (status=:statusValue) DESC;",
           countQuery = "SELECT count(*) FROM bloodmanagement.blood_request",
           nativeQuery = true)
    Page<BloodRequest> findByPending(@Param("statusValue") String statusValue, Pageable pageable);

    @Query(value = "SELECT * FROM bloodmanagement.blood_request order by (status=:statusValue) DESC;", nativeQuery = true)
    List<BloodRequest> findByPending(@Param("statusValue") String statusValue);

    long countByStatus(String status);

    @Modifying
    @Query("UPDATE BloodRequest b SET b.status = 'Rejected' WHERE b.status = 'Pending' AND b.requestDate < :cutoff")
    int rejectStaleRequests(@Param("cutoff") LocalDateTime cutoff);
}

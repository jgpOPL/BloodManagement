package com.example.bloodmanagement.repository;

import com.example.bloodmanagement.domain.PasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {

    // Latest 3 password hashes for a user, newest first
    @Query("SELECT p FROM PasswordHistory p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    List<PasswordHistory> findTop3ByUserIdOrderByCreatedAtDesc(Long userId);

    // Count how many history records exist for a user
    long countByUserId(Long userId);

    // Delete the oldest record for a user (to keep only 3)
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM password_history WHERE user_id = :userId ORDER BY created_at ASC LIMIT 1", nativeQuery = true)
    void deleteOldestByUserId(Long userId);
}

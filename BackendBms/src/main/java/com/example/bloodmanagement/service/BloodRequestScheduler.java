package com.example.bloodmanagement.service;

import com.example.bloodmanagement.repository.BloodRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BloodRequestScheduler {

    @Autowired
    private BloodRequestRepository bloodRequestRepo;

    // Runs every day at 00:05 AM — 5 min after midnight so MySQL event runs first
    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void autoRejectStaleRequests() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        int count = bloodRequestRepo.rejectStaleRequests(cutoff);
        System.out.println("[Scheduler] Auto-rejected " + count + " stale blood requests older than 7 days.");
    }
}

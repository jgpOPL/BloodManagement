package com.example.bloodmanagement.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummaryDto {
    private long totalUsers;
    private long totalBloodRequests;
    private long pendingBloodRequests;
    private long approvedBloodRequests;
    private long pendingDonors;
    private long approvedDonors;
}

package com.GL.CRM.analytics.controller;

import com.GL.CRM.analytics.dto.DistributionEntry;
import com.GL.CRM.analytics.dto.MonthlyCountDTO;
import com.GL.CRM.analytics.dto.OverviewResponse;
import com.GL.CRM.analytics.dto.TaskByUserDTO;
import com.GL.CRM.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/overview")
    public ResponseEntity<OverviewResponse> getOverview() {
        return ResponseEntity.ok(analyticsService.getOverview());
    }

    @GetMapping("/customers/growth")
    public ResponseEntity<List<MonthlyCountDTO>> getCustomerGrowth(
            @RequestParam(defaultValue = "6") int months) {
        return ResponseEntity.ok(analyticsService.getCustomerGrowth(months));
    }

    @GetMapping("/tasks/status")
    public ResponseEntity<List<DistributionEntry>> getTaskStatusDistribution() {
        return ResponseEntity.ok(analyticsService.getTaskStatusDistribution());
    }

    @GetMapping("/tasks/priority")
    public ResponseEntity<List<DistributionEntry>> getTaskPriorityDistribution() {
        return ResponseEntity.ok(analyticsService.getTaskPriorityDistribution());
    }

    @GetMapping("/tasks/type")
    public ResponseEntity<List<DistributionEntry>> getTaskTypeDistribution() {
        return ResponseEntity.ok(analyticsService.getTaskTypeDistribution());
    }

    @GetMapping("/tasks/workload")
    public ResponseEntity<List<TaskByUserDTO>> getTaskWorkloadByUser() {
        return ResponseEntity.ok(analyticsService.getTaskWorkloadByUser());
    }

    @GetMapping("/campaigns/status")
    public ResponseEntity<List<DistributionEntry>> getCampaignStatusDistribution() {
        return ResponseEntity.ok(analyticsService.getCampaignStatusDistribution());
    }

    @GetMapping("/campaigns/monthly")
    public ResponseEntity<List<MonthlyCountDTO>> getCampaignMonthlyTrend(
            @RequestParam(defaultValue = "6") int months) {
        return ResponseEntity.ok(analyticsService.getCampaignMonthlyTrend(months));
    }
}

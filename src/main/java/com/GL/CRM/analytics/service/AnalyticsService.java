package com.GL.CRM.analytics.service;

import com.GL.CRM.analytics.dto.DistributionEntry;
import com.GL.CRM.analytics.dto.MonthlyCountDTO;
import com.GL.CRM.analytics.dto.OverviewResponse;
import com.GL.CRM.analytics.dto.TaskByUserDTO;
import com.GL.CRM.campaign.CampaignRepository;
import com.GL.CRM.campaign.CampaignStatus;
import com.GL.CRM.customer.repository.CustomerRepository;
import com.GL.CRM.task.entity.TaskStatus;
import com.GL.CRM.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final CustomerRepository customerRepository;
    private final TaskRepository taskRepository;
    private final CampaignRepository campaignRepository;

    private static final List<TaskStatus> COMPLETED_STATUSES = List.of(TaskStatus.DONE, TaskStatus.CANCELLED);

    public OverviewResponse getOverview() {
        long totalCustomers = customerRepository.count();
        LocalDateTime startOfMonth = LocalDateTime.now()
                .withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        long newCustomersThisMonth = customerRepository.countSince(startOfMonth);

        long totalCampaigns = campaignRepository.count();
        long sentCampaigns = campaignRepository.countByStatus(CampaignStatus.SENT);
        long draftCampaigns = campaignRepository.countByStatus(CampaignStatus.DRAFT);

        long totalTasks = taskRepository.count();
        long overdueTasks = taskRepository.countOverdue(COMPLETED_STATUSES);

        long todoTasks = 0, inProgressTasks = 0, doneTasks = 0, cancelledTasks = 0;
        for (Object[] row : taskRepository.countGroupedByStatus()) {
            TaskStatus status = (TaskStatus) row[0];
            long count = ((Number) row[1]).longValue();
            switch (status) {
                case TODO -> todoTasks = count;
                case IN_PROGRESS -> inProgressTasks = count;
                case DONE -> doneTasks = count;
                case CANCELLED -> cancelledTasks = count;
            }
        }

        return new OverviewResponse(
                totalCustomers, newCustomersThisMonth,
                totalCampaigns, sentCampaigns, draftCampaigns,
                totalTasks, todoTasks, inProgressTasks, doneTasks, cancelledTasks, overdueTasks
        );
    }

    public List<MonthlyCountDTO> getCustomerGrowth(int months) {
        LocalDateTime since = LocalDateTime.now().minusMonths(months);
        return customerRepository.countByMonthSince(since).stream()
                .map(this::toMonthlyCount)
                .toList();
    }

    public List<DistributionEntry> getTaskStatusDistribution() {
        return taskRepository.countGroupedByStatus().stream()
                .map(row -> new DistributionEntry(row[0].toString(), ((Number) row[1]).longValue()))
                .toList();
    }

    public List<DistributionEntry> getTaskPriorityDistribution() {
        return taskRepository.countGroupedByPriority().stream()
                .map(row -> new DistributionEntry(row[0].toString(), ((Number) row[1]).longValue()))
                .toList();
    }

    public List<DistributionEntry> getTaskTypeDistribution() {
        return taskRepository.countGroupedByType().stream()
                .map(row -> new DistributionEntry(row[0].toString(), ((Number) row[1]).longValue()))
                .toList();
    }

    public List<TaskByUserDTO> getTaskWorkloadByUser() {
        return taskRepository.countGroupedByUser().stream()
                .map(row -> new TaskByUserDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        ((Number) row[2]).longValue()))
                .toList();
    }

    public List<DistributionEntry> getCampaignStatusDistribution() {
        return List.of(
                new DistributionEntry("DRAFT", campaignRepository.countByStatus(CampaignStatus.DRAFT)),
                new DistributionEntry("SENT", campaignRepository.countByStatus(CampaignStatus.SENT))
        );
    }

    public List<MonthlyCountDTO> getCampaignMonthlyTrend(int months) {
        LocalDateTime since = LocalDateTime.now().minusMonths(months);
        return campaignRepository.countByMonthSince(since).stream()
                .map(this::toMonthlyCount)
                .toList();
    }

    private MonthlyCountDTO toMonthlyCount(Object[] row) {
        int year = ((Number) row[0]).intValue();
        int month = ((Number) row[1]).intValue();
        long count = ((Number) row[2]).longValue();
        String label = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + year;
        return new MonthlyCountDTO(year, month, label, count);
    }
}

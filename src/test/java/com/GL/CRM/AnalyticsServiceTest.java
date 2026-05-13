package com.GL.CRM;

import com.GL.CRM.analytics.dto.DistributionEntry;
import com.GL.CRM.analytics.dto.MonthlyCountDTO;
import com.GL.CRM.analytics.dto.OverviewResponse;
import com.GL.CRM.analytics.dto.TaskByUserDTO;
import com.GL.CRM.analytics.service.AnalyticsService;
import com.GL.CRM.campaign.CampaignRepository;
import com.GL.CRM.campaign.CampaignStatus;
import com.GL.CRM.customer.repository.CustomerRepository;
import com.GL.CRM.task.entity.TaskPriority;
import com.GL.CRM.task.entity.TaskStatus;
import com.GL.CRM.task.entity.TaskType;
import com.GL.CRM.task.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    // -------------------------------------------------------------------------
    // getOverview
    // -------------------------------------------------------------------------

    @Test
    void getOverview_shouldReturnCorrectTotals() {
        when(customerRepository.count()).thenReturn(50L);
        when(customerRepository.countSince(any(LocalDateTime.class))).thenReturn(5L);
        when(campaignRepository.count()).thenReturn(10L);
        when(campaignRepository.countByStatus(CampaignStatus.SENT)).thenReturn(7L);
        when(campaignRepository.countByStatus(CampaignStatus.DRAFT)).thenReturn(3L);
        when(taskRepository.count()).thenReturn(30L);
        when(taskRepository.countOverdue(anyList())).thenReturn(2L);
        when(taskRepository.countGroupedByStatus()).thenReturn(List.of(
                new Object[]{TaskStatus.TODO, 10L},
                new Object[]{TaskStatus.IN_PROGRESS, 8L},
                new Object[]{TaskStatus.DONE, 10L},
                new Object[]{TaskStatus.CANCELLED, 2L}
        ));

        OverviewResponse result = analyticsService.getOverview();

        assertThat(result.totalCustomers()).isEqualTo(50L);
        assertThat(result.newCustomersThisMonth()).isEqualTo(5L);
        assertThat(result.totalCampaigns()).isEqualTo(10L);
        assertThat(result.sentCampaigns()).isEqualTo(7L);
        assertThat(result.draftCampaigns()).isEqualTo(3L);
        assertThat(result.totalTasks()).isEqualTo(30L);
        assertThat(result.overdueTasks()).isEqualTo(2L);
    }

    @Test
    void getOverview_shouldMapTaskStatusCountsCorrectly() {
        when(customerRepository.count()).thenReturn(0L);
        when(customerRepository.countSince(any(LocalDateTime.class))).thenReturn(0L);
        when(campaignRepository.count()).thenReturn(0L);
        when(campaignRepository.countByStatus(any())).thenReturn(0L);
        when(taskRepository.count()).thenReturn(20L);
        when(taskRepository.countOverdue(anyList())).thenReturn(0L);
        when(taskRepository.countGroupedByStatus()).thenReturn(List.of(
                new Object[]{TaskStatus.TODO, 5L},
                new Object[]{TaskStatus.IN_PROGRESS, 3L},
                new Object[]{TaskStatus.DONE, 10L},
                new Object[]{TaskStatus.CANCELLED, 2L}
        ));

        OverviewResponse result = analyticsService.getOverview();

        assertThat(result.todoTasks()).isEqualTo(5L);
        assertThat(result.inProgressTasks()).isEqualTo(3L);
        assertThat(result.doneTasks()).isEqualTo(10L);
        assertThat(result.cancelledTasks()).isEqualTo(2L);
    }

    @Test
    void getOverview_shouldReturnZeroTaskCounts_whenNoTasksExist() {
        when(customerRepository.count()).thenReturn(0L);
        when(customerRepository.countSince(any(LocalDateTime.class))).thenReturn(0L);
        when(campaignRepository.count()).thenReturn(0L);
        when(campaignRepository.countByStatus(any())).thenReturn(0L);
        when(taskRepository.count()).thenReturn(0L);
        when(taskRepository.countOverdue(anyList())).thenReturn(0L);
        when(taskRepository.countGroupedByStatus()).thenReturn(List.of());

        OverviewResponse result = analyticsService.getOverview();

        assertThat(result.todoTasks()).isEqualTo(0L);
        assertThat(result.inProgressTasks()).isEqualTo(0L);
        assertThat(result.doneTasks()).isEqualTo(0L);
        assertThat(result.cancelledTasks()).isEqualTo(0L);
    }

    // -------------------------------------------------------------------------
    // getCustomerGrowth
    // -------------------------------------------------------------------------

    @Test
    void getCustomerGrowth_shouldReturnMonthlyList() {
        when(customerRepository.countByMonthSince(any(LocalDateTime.class))).thenReturn(List.of(
                new Object[]{2026, 4, 5L},
                new Object[]{2026, 5, 8L}
        ));

        List<MonthlyCountDTO> result = analyticsService.getCustomerGrowth(6);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).year()).isEqualTo(2026);
        assertThat(result.get(0).month()).isEqualTo(4);
        assertThat(result.get(0).label()).isEqualTo("Apr 2026");
        assertThat(result.get(0).count()).isEqualTo(5L);
        assertThat(result.get(1).label()).isEqualTo("May 2026");
    }

    @Test
    void getCustomerGrowth_shouldReturnEmptyList_whenNoData() {
        when(customerRepository.countByMonthSince(any(LocalDateTime.class))).thenReturn(List.of());

        List<MonthlyCountDTO> result = analyticsService.getCustomerGrowth(6);

        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------------------------
    // getTaskStatusDistribution
    // -------------------------------------------------------------------------

    @Test
    void getTaskStatusDistribution_shouldReturnEntriesForEachStatus() {
        when(taskRepository.countGroupedByStatus()).thenReturn(List.of(
                new Object[]{TaskStatus.TODO, 10L},
                new Object[]{TaskStatus.IN_PROGRESS, 5L},
                new Object[]{TaskStatus.DONE, 20L},
                new Object[]{TaskStatus.CANCELLED, 2L}
        ));

        List<DistributionEntry> result = analyticsService.getTaskStatusDistribution();

        assertThat(result).hasSize(4);
        assertThat(result.get(0).label()).isEqualTo("TODO");
        assertThat(result.get(0).count()).isEqualTo(10L);
        assertThat(result.get(2).label()).isEqualTo("DONE");
        assertThat(result.get(2).count()).isEqualTo(20L);
    }

    @Test
    void getTaskStatusDistribution_shouldReturnEmptyList_whenNoTasks() {
        when(taskRepository.countGroupedByStatus()).thenReturn(List.of());

        List<DistributionEntry> result = analyticsService.getTaskStatusDistribution();

        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------------------------
    // getTaskPriorityDistribution
    // -------------------------------------------------------------------------

    @Test
    void getTaskPriorityDistribution_shouldReturnEntriesForEachPriority() {
        when(taskRepository.countGroupedByPriority()).thenReturn(List.of(
                new Object[]{TaskPriority.LOW, 5L},
                new Object[]{TaskPriority.MEDIUM, 15L},
                new Object[]{TaskPriority.HIGH, 8L},
                new Object[]{TaskPriority.URGENT, 3L}
        ));

        List<DistributionEntry> result = analyticsService.getTaskPriorityDistribution();

        assertThat(result).hasSize(4);
        assertThat(result.get(1).label()).isEqualTo("MEDIUM");
        assertThat(result.get(1).count()).isEqualTo(15L);
        assertThat(result.get(3).label()).isEqualTo("URGENT");
    }

    // -------------------------------------------------------------------------
    // getTaskTypeDistribution
    // -------------------------------------------------------------------------

    @Test
    void getTaskTypeDistribution_shouldReturnEntriesForEachType() {
        when(taskRepository.countGroupedByType()).thenReturn(List.of(
                new Object[]{TaskType.CALL, 10L},
                new Object[]{TaskType.MEETING, 4L},
                new Object[]{TaskType.EMAIL, 12L},
                new Object[]{TaskType.FOLLOW_UP, 7L},
                new Object[]{TaskType.DEMO, 3L}
        ));

        List<DistributionEntry> result = analyticsService.getTaskTypeDistribution();

        assertThat(result).hasSize(5);
        assertThat(result.get(0).label()).isEqualTo("CALL");
        assertThat(result.get(0).count()).isEqualTo(10L);
        assertThat(result.get(2).label()).isEqualTo("EMAIL");
    }

    // -------------------------------------------------------------------------
    // getTaskWorkloadByUser
    // -------------------------------------------------------------------------

    @Test
    void getTaskWorkloadByUser_shouldReturnUserTaskCounts() {
        when(taskRepository.countGroupedByUser()).thenReturn(List.of(
                new Object[]{1L, "Alice", 25L},
                new Object[]{2L, "Bob", 18L}
        ));

        List<TaskByUserDTO> result = analyticsService.getTaskWorkloadByUser();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).userId()).isEqualTo(1L);
        assertThat(result.get(0).userName()).isEqualTo("Alice");
        assertThat(result.get(0).taskCount()).isEqualTo(25L);
        assertThat(result.get(1).userName()).isEqualTo("Bob");
    }

    @Test
    void getTaskWorkloadByUser_shouldReturnEmptyList_whenNoAssignedTasks() {
        when(taskRepository.countGroupedByUser()).thenReturn(List.of());

        List<TaskByUserDTO> result = analyticsService.getTaskWorkloadByUser();

        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------------------------
    // getCampaignStatusDistribution
    // -------------------------------------------------------------------------

    @Test
    void getCampaignStatusDistribution_shouldReturnDraftAndSentCounts() {
        when(campaignRepository.countByStatus(CampaignStatus.DRAFT)).thenReturn(3L);
        when(campaignRepository.countByStatus(CampaignStatus.SENT)).thenReturn(7L);

        List<DistributionEntry> result = analyticsService.getCampaignStatusDistribution();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).label()).isEqualTo("DRAFT");
        assertThat(result.get(0).count()).isEqualTo(3L);
        assertThat(result.get(1).label()).isEqualTo("SENT");
        assertThat(result.get(1).count()).isEqualTo(7L);
    }

    @Test
    void getCampaignStatusDistribution_shouldReturnZeroCounts_whenNoCampaigns() {
        when(campaignRepository.countByStatus(CampaignStatus.DRAFT)).thenReturn(0L);
        when(campaignRepository.countByStatus(CampaignStatus.SENT)).thenReturn(0L);

        List<DistributionEntry> result = analyticsService.getCampaignStatusDistribution();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).count()).isEqualTo(0L);
        assertThat(result.get(1).count()).isEqualTo(0L);
    }

    // -------------------------------------------------------------------------
    // getCampaignMonthlyTrend
    // -------------------------------------------------------------------------

    @Test
    void getCampaignMonthlyTrend_shouldReturnMonthlyList() {
        when(campaignRepository.countByMonthSince(any(LocalDateTime.class))).thenReturn(List.of(
                new Object[]{2026, 1, 2L},
                new Object[]{2026, 3, 4L},
                new Object[]{2026, 5, 1L}
        ));

        List<MonthlyCountDTO> result = analyticsService.getCampaignMonthlyTrend(6);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).label()).isEqualTo("Jan 2026");
        assertThat(result.get(0).count()).isEqualTo(2L);
        assertThat(result.get(1).label()).isEqualTo("Mar 2026");
        assertThat(result.get(2).label()).isEqualTo("May 2026");
        assertThat(result.get(2).count()).isEqualTo(1L);
    }

    @Test
    void getCampaignMonthlyTrend_shouldReturnEmptyList_whenNoData() {
        when(campaignRepository.countByMonthSince(any(LocalDateTime.class))).thenReturn(List.of());

        List<MonthlyCountDTO> result = analyticsService.getCampaignMonthlyTrend(6);

        assertThat(result).isEmpty();
    }
}

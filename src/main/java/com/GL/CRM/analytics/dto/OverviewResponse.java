package com.GL.CRM.analytics.dto;

public record OverviewResponse(
        long totalCustomers,
        long newCustomersThisMonth,
        long totalCampaigns,
        long sentCampaigns,
        long draftCampaigns,
        long totalTasks,
        long todoTasks,
        long inProgressTasks,
        long doneTasks,
        long cancelledTasks,
        long overdueTasks
) {}

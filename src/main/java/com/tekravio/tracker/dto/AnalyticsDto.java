package com.tekravio.tracker.dto;

import com.tekravio.tracker.model.TaskPriority;
import com.tekravio.tracker.model.TaskStatus;

import java.math.BigDecimal;
import java.util.Map;

public final class AnalyticsDto {

    private AnalyticsDto() {
    }

    public record SprintSummary(
            long totalTasks,
            double completedPercentage,
            double averageCompletionHours,
            Map<TaskPriority, Long> tasksByPriority,
            long overdueTasks) {
    }

    public record EngineerWorkload(
            long activeTasks,
            Map<TaskStatus, Long> tasksByStatus,
            BigDecimal totalEstimatedHours,
            BigDecimal totalActualHours) {
    }

    public record ProjectHealth(
            int score,
            double completionPercentage,
            long overdueTasks,
            String formula) {
    }
}

package com.tekravio.tracker.dto;

import com.tekravio.tracker.model.TaskStatus;

import java.time.LocalDateTime;

public record TaskHistoryDto(
        Long id,
        Long taskId,
        TaskStatus previousStatus,
        TaskStatus newStatus,
        LocalDateTime changedAt,
        String changedBy) {
}

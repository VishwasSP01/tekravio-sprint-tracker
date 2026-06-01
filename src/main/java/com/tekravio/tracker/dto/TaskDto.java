package com.tekravio.tracker.dto;

import com.tekravio.tracker.model.TaskPriority;
import com.tekravio.tracker.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class TaskDto {

    private TaskDto() {
    }

    @Schema(name = "TaskRequest")
    public record Request(
            @NotBlank @Size(max = 255) String title,
            @NotBlank @Size(max = 2000) String description,
            @NotNull TaskPriority priority,
            @NotNull TaskStatus status,
            @NotNull @DecimalMin("0.0") BigDecimal estimatedHours,
            @DecimalMin("0.0") BigDecimal actualHours,
            @NotNull Long sprintId,
            Long assignedEngineerId) {
    }

    @Schema(name = "TaskResponse")
    public record Response(
            Long id,
            String title,
            String description,
            TaskPriority priority,
            TaskStatus status,
            BigDecimal estimatedHours,
            BigDecimal actualHours,
            Long sprintId,
            Long assignedEngineerId,
            LocalDateTime createdAt,
            LocalDateTime completedAt) {
    }

    @Schema(name = "TaskStatusRequest")
    public record StatusRequest(@NotNull TaskStatus status) {
    }
}

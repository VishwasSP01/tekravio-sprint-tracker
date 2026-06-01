package com.tekravio.tracker.dto;

import com.tekravio.tracker.model.SprintStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public final class SprintDto {

    private SprintDto() {
    }

    @Schema(name = "SprintRequest")
    public record Request(
            @Positive int sprintNumber,
            @NotBlank @Size(max = 1000) String goal,
            @NotNull SprintStatus status,
            @NotNull LocalDate startDate,
            @NotNull LocalDate endDate,
            @NotNull Long projectId) {
    }

    @Schema(name = "SprintResponse")
    public record Response(
            Long id,
            int sprintNumber,
            String goal,
            SprintStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Long projectId) {
    }
}

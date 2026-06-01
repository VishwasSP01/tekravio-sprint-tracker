package com.tekravio.tracker.dto;

import com.tekravio.tracker.model.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public final class ProjectDto {

    private ProjectDto() {
    }

    @Schema(name = "ProjectRequest")
    public record Request(
            @NotBlank @Size(max = 255) String name,
            @NotBlank @Size(max = 2000) String description,
            @NotNull ProjectStatus status,
            @NotNull LocalDate startDate,
            LocalDate endDate,
            @NotNull Long clientId) {
    }

    @Schema(name = "ProjectResponse")
    public record Response(
            Long id,
            String name,
            String description,
            ProjectStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Long clientId) {
    }
}

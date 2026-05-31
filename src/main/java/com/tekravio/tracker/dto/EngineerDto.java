package com.tekravio.tracker.dto;

import com.tekravio.tracker.model.PrimaryStack;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public final class EngineerDto {

    private EngineerDto() {
    }

    public record Request(
            @NotBlank @Size(max = 255) String name,
            @NotBlank @Email @Size(max = 255) String email,
            @NotNull PrimaryStack primaryStack,
            @PositiveOrZero int experienceYears,
            boolean available) {
    }

    public record Response(
            Long id,
            String name,
            String email,
            PrimaryStack primaryStack,
            int experienceYears,
            boolean available) {
    }
}

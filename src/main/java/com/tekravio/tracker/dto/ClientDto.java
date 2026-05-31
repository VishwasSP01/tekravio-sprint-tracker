package com.tekravio.tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class ClientDto {

    private ClientDto() {
    }

    public record Request(
            @NotBlank @Size(max = 255) String name,
            @NotBlank @Size(max = 255) String industry,
            @NotBlank @Email @Size(max = 255) String contactEmail,
            @NotBlank @Size(max = 255) String country) {
    }

    public record Response(
            Long id,
            String name,
            String industry,
            String contactEmail,
            String country,
            LocalDateTime createdAt) {
    }
}

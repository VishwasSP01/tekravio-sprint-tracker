package com.tekravio.tracker.dto;

import com.tekravio.tracker.model.UserRole;
import jakarta.validation.constraints.NotBlank;

public final class AuthDto {

    private AuthDto() {
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    public record LoginResponse(String token, String tokenType, long expiresInMinutes, UserRole role) {
    }
}

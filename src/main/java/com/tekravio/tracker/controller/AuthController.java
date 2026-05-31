package com.tekravio.tracker.controller;

import com.tekravio.tracker.dto.ApiResponse;
import com.tekravio.tracker.dto.AuthDto;
import com.tekravio.tracker.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    ApiResponse<AuthDto.LoginResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        return ApiResponse.success(service.login(request), "Login successful");
    }
}

package com.tekravio.tracker.service;

import com.tekravio.tracker.dto.AuthDto;
import com.tekravio.tracker.model.AppUser;
import com.tekravio.tracker.repository.AppUserRepository;
import com.tekravio.tracker.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final AppUserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
                       AppUserRepository userRepository, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        UserDetails details = userDetailsService.loadUserByUsername(request.username());
        AppUser user = userRepository.findByUsername(request.username()).orElseThrow();
        return new AuthDto.LoginResponse(jwtService.generateToken(details), "Bearer",
                jwtService.expirationMinutes(), user.getRole());
    }
}

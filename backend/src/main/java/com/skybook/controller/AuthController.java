package com.skybook.controller;

import com.skybook.dto.auth.AuthResponse;
import com.skybook.dto.auth.LoginRequest;
import com.skybook.dto.auth.RefreshRequest;
import com.skybook.dto.auth.RegisterRequest;
import com.skybook.dto.common.MessageResponse;
import com.skybook.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new customer")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        return authService.register(request, httpRequest);
    }

    @PostMapping("/login")
    @Operation(summary = "Login and obtain JWT")
    public AuthResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        return authService.login(request, httpRequest);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request, HttpServletRequest httpRequest) {
        return authService.refresh(request.getRefreshToken(), httpRequest);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout and revoke refresh token")
    public MessageResponse logout(@RequestBody(required = false) RefreshRequest request, HttpServletRequest httpRequest) {
        authService.logout(request != null ? request.getRefreshToken() : null, httpRequest);
        return MessageResponse.builder().message("Logged out").build();
    }
}

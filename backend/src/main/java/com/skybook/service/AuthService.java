package com.skybook.service;

import com.skybook.config.SkyBookProperties;
import com.skybook.domain.AuditActions;
import com.skybook.domain.RefreshToken;
import com.skybook.domain.Role;
import com.skybook.domain.User;
import com.skybook.domain.UserStatus;
import com.skybook.dto.auth.AuthResponse;
import com.skybook.dto.auth.LoginRequest;
import com.skybook.dto.auth.RegisterRequest;
import com.skybook.exception.BadRequestException;
import com.skybook.exception.ResourceNotFoundException;
import com.skybook.repository.RefreshTokenRepository;
import com.skybook.repository.RoleRepository;
import com.skybook.repository.UserRepository;
import com.skybook.security.JwtService;
import com.skybook.util.IdUtils;
import com.skybook.util.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final SkyBookProperties properties;
    private final AuditService auditService;
    private final MapperService mapperService;

    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
        long start = System.currentTimeMillis();
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException("CUSTOMER role missing — run seed.sql"));

        Instant now = Instant.now();
        User user = User.builder()
                .id(IdUtils.uuid())
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(customerRole)
                .status(UserStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();
        userRepository.save(user);

        AuthResponse response = issueTokens(user, httpRequest);
        auditService.recordForUser(
                user,
                AuditActions.USER_REGISTER,
                "/api/v1/auth/register",
                "POST",
                201,
                (int) (System.currentTimeMillis() - start),
                "{\"username\":\"" + user.getUsername() + "\"}",
                httpRequest,
                null
        );
        return response;
    }

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        long start = System.currentTimeMillis();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        AuthResponse response = issueTokens(user, httpRequest);
        auditService.recordForUser(
                user,
                AuditActions.USER_LOGIN,
                "/api/v1/auth/login",
                "POST",
                200,
                (int) (System.currentTimeMillis() - start),
                "{\"result\":\"success\"}",
                httpRequest,
                null
        );
        return response;
    }

    @Transactional
    public AuthResponse refresh(String refreshTokenRaw, HttpServletRequest httpRequest) {
        String hash = IdUtils.sha256(refreshTokenRaw);
        RefreshToken stored = refreshTokenRepository.findByTokenHashAndRevokedFalse(hash)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));
        if (stored.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Refresh token expired");
        }
        stored.setRevoked(true);
        stored.setRevokedAt(Instant.now());
        refreshTokenRepository.save(stored);

        return issueTokens(stored.getUser(), httpRequest);
    }

    @Transactional
    public void logout(String refreshTokenRaw, HttpServletRequest httpRequest) {
        long start = System.currentTimeMillis();
        if (refreshTokenRaw != null && !refreshTokenRaw.isBlank()) {
            refreshTokenRepository.findByTokenHashAndRevokedFalse(IdUtils.sha256(refreshTokenRaw))
                    .ifPresent(token -> {
                        token.setRevoked(true);
                        token.setRevokedAt(Instant.now());
                        refreshTokenRepository.save(token);
                    });
        }
        auditService.record(
                AuditActions.USER_LOGOUT,
                "/api/v1/auth/logout",
                "POST",
                200,
                (int) (System.currentTimeMillis() - start),
                "{\"result\":\"success\"}",
                httpRequest
        );
    }

    private AuthResponse issueTokens(User user, HttpServletRequest httpRequest) {
        String access = jwtService.generateAccessToken(user.getId(), user.getUsername(), user.getRole().getName());
        String refreshRaw = IdUtils.opaqueToken();
        Instant now = Instant.now();
        RefreshToken refreshToken = RefreshToken.builder()
                .id(IdUtils.uuid())
                .user(user)
                .tokenHash(IdUtils.sha256(refreshRaw))
                .expiresAt(now.plus(properties.getJwt().getRefreshTokenDays(), ChronoUnit.DAYS))
                .revoked(false)
                .createdAt(now)
                .userAgent(httpRequest != null ? httpRequest.getHeader("User-Agent") : null)
                .ipAddress(httpRequest != null ? RequestUtils.clientIp(httpRequest) : null)
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refreshRaw)
                .tokenType("Bearer")
                .expiresInMinutes(properties.getJwt().getAccessTokenMinutes())
                .user(mapperService.toUserResponse(user))
                .build();
    }
}

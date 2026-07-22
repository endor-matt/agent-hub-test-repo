package com.skybook.dto.auth;

import com.skybook.dto.user.UserResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresInMinutes;
    private UserResponse user;
}

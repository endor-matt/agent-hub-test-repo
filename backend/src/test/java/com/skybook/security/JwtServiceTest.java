package com.skybook.security;

import com.skybook.config.SkyBookProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        SkyBookProperties props = new SkyBookProperties();
        props.getJwt().setSecret("TestOnlySecretKeyForUnitTests_MustBeAtLeast256BitsLong!!");
        props.getJwt().setAccessTokenMinutes(30);
        jwtService = new JwtService(props);
    }

    @Test
    void generatesAndParsesAccessToken() {
        String token = jwtService.generateAccessToken("user-1", "jdoe", "CUSTOMER");

        assertThat(jwtService.isValid(token)).isTrue();
        assertThat(jwtService.extractUserId(token)).isEqualTo("user-1");
        assertThat(jwtService.extractUsername(token)).isEqualTo("jdoe");
        assertThat(jwtService.extractRole(token)).isEqualTo("CUSTOMER");
        assertThat(jwtService.extractJti(token)).isNotBlank();
    }

    @Test
    void rejectsTamperedToken() {
        String token = jwtService.generateAccessToken("user-1", "jdoe", "CUSTOMER");
        String tampered = token.substring(0, token.length() - 4) + "xxxx";
        assertThat(jwtService.isValid(tampered)).isFalse();
    }
}

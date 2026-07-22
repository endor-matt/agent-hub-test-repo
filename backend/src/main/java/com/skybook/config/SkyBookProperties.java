package com.skybook.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "skybook")
public class SkyBookProperties {

    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();
    private Ai ai = new Ai();
    private Training training = new Training();

    @Getter
    @Setter
    public static class Jwt {
        private String secret;
        private long accessTokenMinutes = 30;
        private long refreshTokenDays = 7;
    }

    @Getter
    @Setter
    public static class Cors {
        private List<String> allowedOrigins = List.of("http://localhost:3000");
    }

    @Getter
    @Setter
    public static class Ai {
        private String baseUrl = "http://localhost:8000";
    }

    @Getter
    @Setter
    public static class Training {
        private boolean enabled = false;
    }
}

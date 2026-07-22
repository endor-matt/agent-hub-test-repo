package com.skybook.training;

import com.skybook.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

/**
 * TRAINING ONLY — secrets, weak crypto, missing authz, XSS, CSRF demos.
 */
@RestController
@RequestMapping("/api/v1/training")
@Profile("training")
@RequiredArgsConstructor
@Tag(name = "Training Auth/Crypto/XSS/CSRF (LAB)", description = TrainingMarkers.NOTICE)
public class TrainingAuthCryptoXssController {

    // INTENTIONALLY VULNERABLE — CWE-798 Hardcoded Secret
    private static final String HARDCODED_PARTNER_KEY = "sk_live_training_DO_NOT_USE_41f6a";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/insecure/secrets")
    @Operation(summary = "INSECURE: returns hardcoded partner API key (CWE-798)")
    public Map<String, String> insecureSecrets() {
        return Map.of(
                "notice", TrainingMarkers.NOTICE,
                "cwe", "CWE-798",
                "partnerApiKey", HARDCODED_PARTNER_KEY
        );
    }

    @GetMapping("/secure/secrets")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "SECURE: secret loaded from environment, admin only")
    public Map<String, String> secureSecrets() {
        String key = System.getenv().getOrDefault("PARTNER_API_KEY", "not-configured");
        return Map.of(
                "partnerApiKeyConfigured", String.valueOf(!"not-configured".equals(key)),
                "hint", "Set PARTNER_API_KEY in the environment; never hardcode"
        );
    }

    @GetMapping("/insecure/crypto")
    @Operation(summary = "INSECURE: MD5 password fingerprint (CWE-328)")
    public Map<String, String> insecureCrypto(@RequestParam String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
        return Map.of(
                "notice", TrainingMarkers.NOTICE,
                "cwe", "CWE-328",
                "md5", HexFormat.of().formatHex(digest)
        );
    }

    @GetMapping("/secure/crypto")
    @Operation(summary = "SECURE: BCrypt hash (one-way, salted)")
    public Map<String, String> secureCrypto(@RequestParam String password) {
        return Map.of("bcrypt", passwordEncoder.encode(password));
    }

    @GetMapping("/insecure/users")
    @Operation(summary = "INSECURE: missing authorization — lists usernames without auth (CWE-862)")
    public List<String> insecureUsers() {
        // INTENTIONALLY VULNERABLE — no authz check (SecurityConfig permits /training/**)
        return userRepository.findAll().stream().map(u -> u.getUsername()).toList();
    }

    @GetMapping("/secure/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "SECURE: admin-only user listing")
    public List<String> secureUsers() {
        return userRepository.findAll().stream().map(u -> u.getUsername()).toList();
    }

    @GetMapping(value = "/insecure/xss", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(summary = "INSECURE: reflects unsanitized input into HTML (CWE-79)")
    public String insecureXss(@RequestParam String q) {
        // INTENTIONALLY VULNERABLE
        return "<html><body><h1>Search</h1><p>Results for: " + q + "</p></body></html>";
    }

    @GetMapping(value = "/secure/xss", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(summary = "SECURE: HTML-escapes reflection")
    public String secureXss(@RequestParam String q) {
        String safe = q
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
        return "<html><body><h1>Search</h1><p>Results for: " + safe + "</p></body></html>";
    }

    @PostMapping("/insecure/csrf/transfer")
    @Operation(summary = "INSECURE: state change trusts cookie identity without CSRF token (CWE-352)")
    public Map<String, Object> insecureCsrf(
            @CookieValue(value = "labSession", required = false) String labSession,
            @RequestParam String toAccount,
            @RequestParam int amount
    ) {
        // INTENTIONALLY VULNERABLE educational pattern (cookie session without anti-CSRF)
        return Map.of(
                "notice", TrainingMarkers.NOTICE,
                "cwe", "CWE-352",
                "session", labSession == null ? "missing" : labSession,
                "toAccount", toAccount,
                "amount", amount,
                "status", "TRANSFERRED_INSECURELY"
        );
    }

    @PostMapping("/secure/csrf/transfer")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "SECURE: requires JWT auth + matching anti-CSRF header")
    public Map<String, Object> secureCsrf(
            @RequestHeader(value = "X-CSRF-Token", required = false) String csrf,
            @RequestParam String toAccount,
            @RequestParam int amount
    ) {
        if (csrf == null || csrf.isBlank()) {
            return Map.of("error", "Missing X-CSRF-Token header");
        }
        return Map.of(
                "status", "ok",
                "toAccount", toAccount,
                "amount", amount,
                "csrfAccepted", true
        );
    }
}

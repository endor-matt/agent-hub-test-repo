package com.skybook.training;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TRAINING ONLY — SQL Injection (CWE-89) and Command Injection (CWE-78).
 * OWASP A03:2021 Injection.
 */
@RestController
@RequestMapping("/api/v1/training")
@Profile("training")
@Tag(name = "Training Injection (LAB)", description = TrainingMarkers.NOTICE)
public class TrainingInjectionController {

    private static final Set<String> ALLOWED_HOSTS = Set.of("127.0.0.1", "localhost");

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/insecure/sql")
    @Operation(summary = "FIXED: bound-parameter native query (was CWE-89 string concatenation)")
    public Map<String, Object> insecureSql(@RequestParam String username) {
        // Remediated (AI SAST CWE-89): bind the parameter instead of concatenating into the SQL string.
        Query query = entityManager.createNativeQuery(
                "SELECT id, username, email FROM users WHERE username = :username"
        );
        query.setParameter("username", username);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();
        return Map.of(
                "notice", TrainingMarkers.NOTICE,
                "cwe", "CWE-89 mitigated",
                "owasp", "A03:2021 Injection",
                "rowCount", rows.size(),
                "hint", "Now matches /secure/sql — bound parameter, no string concatenation"
        );
    }

    @GetMapping("/secure/sql")
    @Operation(summary = "SECURE: parameterized native query")
    public Map<String, Object> secureSql(@RequestParam String username) {
        Query query = entityManager.createNativeQuery(
                "SELECT id, username, email FROM users WHERE username = :username"
        );
        query.setParameter("username", username);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();
        return Map.of(
                "notice", "Secure counterpart — bound parameters",
                "cwe", "CWE-89 mitigated",
                "rowCount", rows.size()
        );
    }

    @GetMapping(value = "/insecure/command", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "FIXED: allowlisted host + argument array (was CWE-78 string-built command)")
    public String insecureCommand(@RequestParam String host) throws Exception {
        // Remediated (AI SAST CWE-78): allowlist the host and pass argv directly, no
        // string-built command, matching /secure/command.
        if (!ALLOWED_HOSTS.contains(host)) {
            return "Rejected host (allowlist only): " + ALLOWED_HOSTS;
        }
        ProcessBuilder pb = new ProcessBuilder("ping", "-c", "1", host);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        return new String(process.getInputStream().readAllBytes());
    }

    @GetMapping(value = "/secure/command", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    @Operation(summary = "SECURE: allowlisted host + argument array (no shell)")
    public String secureCommand(@RequestParam String host) throws Exception {
        if (!ALLOWED_HOSTS.contains(host)) {
            return "Rejected host (allowlist only): " + ALLOWED_HOSTS;
        }
        ProcessBuilder pb = new ProcessBuilder("ping", "-c", "1", host);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        return new String(process.getInputStream().readAllBytes());
    }
}

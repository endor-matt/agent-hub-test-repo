package com.skybook.training;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Catalog of intentional vulnerability demos.
 * Profile: training
 */
@RestController
@RequestMapping("/api/v1/training")
@Profile("training")
@Tag(name = "Training (INSECURE LAB)", description = TrainingMarkers.NOTICE)
public class TrainingCatalogController {

    @GetMapping("/catalog")
    @Operation(summary = "List intentional vulnerability demos (CWE / OWASP mapped)")
    public Map<String, Object> catalog() {
        return Map.of(
                "notice", TrainingMarkers.NOTICE,
                "profile", "training",
                "demos", List.of(
                        entry("sql-injection", "CWE-89", "A03:2021 Injection", "/api/v1/training/insecure/sql", "/api/v1/training/secure/sql"),
                        entry("command-injection", "CWE-78", "A03:2021 Injection", "/api/v1/training/insecure/command", "/api/v1/training/secure/command"),
                        entry("path-traversal", "CWE-22", "A01:2021 Broken Access Control", "/api/v1/training/insecure/path", "/api/v1/training/secure/path"),
                        entry("log4j-user-controlled-log", "CWE-117", "A06:2021 Vulnerable Components", "/api/v1/training/insecure/log4j", "/api/v1/training/secure/log4j"),
                        entry("insecure-deserialization", "CWE-502", "A08:2021 Software and Data Integrity Failures", "/api/v1/training/insecure/deserialize", "/api/v1/training/secure/deserialize"),
                        entry("xxe", "CWE-611", "A05:2021 Security Misconfiguration", "/api/v1/training/insecure/xxe", "/api/v1/training/secure/xxe"),
                        entry("ssrf", "CWE-918", "A10:2021 SSRF", "/api/v1/training/insecure/ssrf", "/api/v1/training/secure/ssrf"),
                        entry("hardcoded-secrets", "CWE-798", "A07:2021 Identification and Authentication Failures", "/api/v1/training/insecure/secrets", "/api/v1/training/secure/secrets"),
                        entry("weak-cryptography", "CWE-328", "A02:2021 Cryptographic Failures", "/api/v1/training/insecure/crypto", "/api/v1/training/secure/crypto"),
                        entry("missing-authorization", "CWE-862", "A01:2021 Broken Access Control", "/api/v1/training/insecure/users", "/api/v1/training/secure/users"),
                        entry("unsafe-file-upload", "CWE-434", "A04:2021 Insecure Design", "/api/v1/training/insecure/upload", "/api/v1/training/secure/upload"),
                        entry("xss", "CWE-79", "A03:2021 Injection", "/api/v1/training/insecure/xss", "/api/v1/training/secure/xss"),
                        entry("csrf", "CWE-352", "A01:2021 Broken Access Control", "/api/v1/training/insecure/csrf/transfer", "/api/v1/training/secure/csrf/transfer")
                )
        );
    }

    private Map<String, String> entry(String id, String cwe, String owasp, String insecure, String secure) {
        return Map.of(
                "id", id,
                "cwe", cwe,
                "owasp", owasp,
                "insecure", insecure,
                "secure", secure
        );
    }
}

package com.skybook.training;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URL;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

/**
 * TRAINING ONLY — SSRF (CWE-918). OWASP A10:2021.
 * Insecure path intentionally uses pinned vulnerable Apache HttpClient.
 */
@RestController
@RequestMapping("/api/v1/training")
@Profile("training")
@Tag(name = "Training SSRF (LAB)", description = TrainingMarkers.NOTICE)
public class TrainingSsrfController {

    private static final Set<String> ALLOWED = Set.of("https://example.com", "https://httpbin.org/get");

    @GetMapping(value = "/insecure/ssrf", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "INSECURE: SSRF via vulnerable Apache HttpClient 4.3.6 (CWE-918)")
    public String insecureSsrf(@RequestParam String url) throws Exception {
        // INTENTIONALLY VULNERABLE — unrestricted outbound fetch using pinned httpclient
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(get)) {
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        }
    }

    @GetMapping("/secure/ssrf")
    @Operation(summary = "SECURE: allowlisted URLs only (java.net)")
    public Map<String, Object> secureSsrf(@RequestParam String url) throws Exception {
        if (!ALLOWED.contains(url)) {
            return Map.of("error", "URL not in allowlist", "allowed", ALLOWED);
        }
        URL target = URI.create(url).toURL();
        try (InputStream in = target.openStream()) {
            byte[] bytes = in.readNBytes(2048);
            return Map.of(
                    "status", "ok",
                    "preview", new String(bytes, StandardCharsets.UTF_8)
            );
        }
    }
}

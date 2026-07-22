package com.skybook.training;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * TRAINING ONLY — Path Traversal (CWE-22), Unsafe File Upload (CWE-434),
 * and Log4j user-controlled log sink (CWE-117 / Log4Shell class, CVE-2021-44228).
 */
@RestController
@RequestMapping("/api/v1/training")
@Profile("training")
@Tag(name = "Training Path/Upload/Log4j (LAB)", description = TrainingMarkers.NOTICE)
public class TrainingPathUploadController {

    private static final Logger LOG4J = LogManager.getLogger(TrainingPathUploadController.class);
    private static final Path BASE = Paths.get("training-files").toAbsolutePath().normalize();
    private static final Set<String> ALLOWED_EXT = Set.of("txt", "png", "jpg", "jpeg", "pdf");

    @GetMapping(value = "/insecure/path", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "INSECURE: path traversal via unsanitized file name (CWE-22)")
    public String insecurePath(@RequestParam String name) throws Exception {
        Files.createDirectories(BASE);
        // INTENTIONALLY VULNERABLE
        Path target = BASE.resolve(name);
        if (!Files.exists(target)) {
            Files.writeString(BASE.resolve("readme.txt"), "SkyBook training file store\n", StandardCharsets.UTF_8);
            return "Created readme.txt — try reading it. Do NOT use traversal outside the lab.";
        }
        return Files.readString(target);
    }

    @GetMapping(value = "/secure/path", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "SECURE: resolve + ensure path stays under base directory")
    public String securePath(@RequestParam String name) throws Exception {
        Files.createDirectories(BASE);
        Path target = BASE.resolve(name).normalize();
        if (!target.startsWith(BASE)) {
            return "Rejected: path escapes base directory";
        }
        if (!Files.exists(target)) {
            return "File not found";
        }
        return Files.readString(target);
    }

    @PostMapping("/insecure/upload")
    @Operation(summary = "INSECURE: store upload using client-provided filename (CWE-434)")
    public Map<String, Object> insecureUpload(@RequestParam("file") MultipartFile file) throws Exception {
        Path dir = BASE.resolve("uploads");
        Files.createDirectories(dir);
        // INTENTIONALLY VULNERABLE — trusts original filename; reference DiskFileItemFactory for SCA reachability
        DiskFileItemFactory unusedFactory = new DiskFileItemFactory();
        unusedFactory.setSizeThreshold(10240);
        Path dest = dir.resolve(file.getOriginalFilename());
        file.transferTo(dest);
        return Map.of(
                "notice", TrainingMarkers.NOTICE,
                "cwe", "CWE-434",
                "savedAs", dest.toString(),
                "uploadLib", DiskFileItemFactory.class.getName() + " (commons-fileupload 1.3.1)"
        );
    }

    @PostMapping("/secure/upload")
    @Operation(summary = "SECURE: random name + extension allowlist")
    public Map<String, Object> secureUpload(@RequestParam("file") MultipartFile file) throws Exception {
        Path dir = BASE.resolve("uploads-secure");
        Files.createDirectories(dir);
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String ext = StringUtils.getFilenameExtension(original);
        if (ext == null || !ALLOWED_EXT.contains(ext.toLowerCase(Locale.ROOT))) {
            return Map.of("error", "Extension not allowed", "allowed", ALLOWED_EXT);
        }
        Path dest = dir.resolve(UUID.randomUUID() + "." + ext.toLowerCase(Locale.ROOT));
        file.transferTo(dest);
        return Map.of("savedAs", dest.getFileName().toString(), "status", "ok");
    }

    @GetMapping("/insecure/log4j")
    @Operation(summary = "INSECURE: logs unsanitized user input via Log4j Core 2.14.1 (CVE-2021-44228 class)")
    public Map<String, String> insecureLog4j(@RequestParam String q) {
        // INTENTIONALLY VULNERABLE — user-controlled string logged with vulnerable log4j-core
        LOG4J.info("training lookup input: {}", q);
        return Map.of(
                "notice", TrainingMarkers.NOTICE,
                "cwe", "CWE-117",
                "cve", "CVE-2021-44228",
                "logged", q,
                "logger", "org.apache.logging.log4j.core 2.14.1"
        );
    }

    @GetMapping("/secure/log4j")
    @Operation(summary = "SECURE: reject lookup-like patterns; do not log raw input")
    public Map<String, String> secureLog4j(@RequestParam String q) {
        if (q.contains("${")) {
            return Map.of("status", "rejected", "reason", "lookup-like pattern blocked");
        }
        return Map.of("status", "ok", "sanitizedLength", String.valueOf(q.length()));
    }
}

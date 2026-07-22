package com.skybook.training;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.collections.CollectionUtils;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Map;

/**
 * TRAINING ONLY — Insecure Deserialization (CWE-502) and XXE (CWE-611).
 */
@RestController
@RequestMapping("/api/v1/training")
@Profile("training")
@Tag(name = "Training Deserialize/XXE (LAB)", description = TrainingMarkers.NOTICE)
public class TrainingDeserializeXxeController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/insecure/deserialize")
    @Operation(summary = "INSECURE: Java native deserialization (CWE-502)")
    public Map<String, Object> insecureDeserialize(@RequestParam("file") MultipartFile file) throws Exception {
        // INTENTIONALLY VULNERABLE — never deserialize untrusted bytes in production
        try (ObjectInputStream ois = new ObjectInputStream(file.getInputStream())) {
            Object value = ois.readObject();
            return Map.of(
                    "notice", TrainingMarkers.NOTICE,
                    "cwe", "CWE-502",
                    "owasp", "A08:2021 Software and Data Integrity Failures",
                    "type", value == null ? "null" : value.getClass().getName(),
                    "commonsCollectionsOnClasspath", CollectionUtils.class.getName(),
                    "hint", "Use /secure/deserialize with JSON DTO instead"
            );
        }
    }

    public record NameDto(String name) {
    }

    @PostMapping(value = "/secure/deserialize", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "SECURE: JSON binding to an explicit DTO")
    public Map<String, Object> secureDeserialize(@RequestBody NameDto body) {
        return Map.of(
                "notice", "Secure counterpart — typed JSON, no ObjectInputStream",
                "name", body.name() == null ? "" : body.name()
        );
    }

    @PostMapping(value = "/insecure/xxe", consumes = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "INSECURE: XML parse with external entities enabled (CWE-611)")
    public Map<String, Object> insecureXxe(@RequestBody String xml) throws Exception {
        // INTENTIONALLY VULNERABLE
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // defaults often allow XXE depending on parser — intentionally not hardened
        Document doc = factory.newDocumentBuilder()
                .parse(new ByteArrayInputStream(xml.getBytes()));
        String root = doc.getDocumentElement() != null ? doc.getDocumentElement().getNodeName() : "none";
        return Map.of(
                "notice", TrainingMarkers.NOTICE,
                "cwe", "CWE-611",
                "owasp", "A05:2021 Security Misconfiguration",
                "rootElement", root
        );
    }

    @PostMapping(value = "/secure/xxe", consumes = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "SECURE: XXE protections enabled on DocumentBuilderFactory")
    public Map<String, Object> secureXxe(@RequestBody String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setExpandEntityReferences(false);
        Document doc = factory.newDocumentBuilder()
                .parse(new ByteArrayInputStream(xml.getBytes()));
        String root = doc.getDocumentElement() != null ? doc.getDocumentElement().getNodeName() : "none";
        return Map.of("rootElement", root, "status", "parsed-safely");
    }

    @PostMapping("/secure/deserialize/echo-json")
    @Operation(summary = "SECURE helper: demonstrate Jackson DTO round-trip")
    public Map<String, Object> echoJson(@RequestBody Map<String, Object> body) throws Exception {
        String json = objectMapper.writeValueAsString(body);
        return Map.of("json", json);
    }
}

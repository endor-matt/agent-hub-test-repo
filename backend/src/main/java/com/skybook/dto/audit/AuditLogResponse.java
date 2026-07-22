package com.skybook.dto.audit;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AuditLogResponse {
    private String auditId;
    private Instant timestamp;
    private String username;
    private String userId;
    private String role;
    private String ipAddress;
    private String sessionId;
    private String action;
    private String resource;
    private String httpMethod;
    private Integer responseStatus;
    private String browser;
    private String operatingSystem;
    private Integer executionTimeMs;
    private String details;
}

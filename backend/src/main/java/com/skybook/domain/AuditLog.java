package com.skybook.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(length = 64)
    private String username;

    @Column(name = "user_id", length = 36)
    private String userId;

    @Column(length = 32)
    private String role;

    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    @Column(name = "session_id", length = 128)
    private String sessionId;

    @Column(nullable = false, length = 64)
    private String action;

    @Column(length = 512)
    private String resource;

    @Column(name = "http_method", length = 16)
    private String httpMethod;

    @Column(name = "response_status")
    private Integer responseStatus;

    @Column(length = 128)
    private String browser;

    @Column(name = "operating_system", length = 128)
    private String operatingSystem;

    @Column(name = "execution_time_ms")
    private Integer executionTimeMs;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String details;
}

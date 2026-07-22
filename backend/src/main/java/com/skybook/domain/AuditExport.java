package com.skybook.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "audit_exports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditExport {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "export_type", nullable = false, length = 16)
    private ExportType exportType;

    @Column(name = "date_from")
    private LocalDate dateFrom;

    @Column(name = "date_to")
    private LocalDate dateTo;

    @Column(name = "filter_username", length = 64)
    private String filterUsername;

    @Column(name = "filter_action", length = 64)
    private String filterAction;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "row_count", nullable = false)
    private Integer rowCount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}

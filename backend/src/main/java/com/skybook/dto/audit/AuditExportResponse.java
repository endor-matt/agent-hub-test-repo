package com.skybook.dto.audit;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class AuditExportResponse {
    private String id;
    private String exportType;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String filterUsername;
    private String filterAction;
    private String fileName;
    private Integer rowCount;
    private Instant createdAt;
    private String requestedByUsername;
}

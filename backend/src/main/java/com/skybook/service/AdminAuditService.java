package com.skybook.service;

import com.skybook.domain.AuditActions;
import com.skybook.domain.AuditExport;
import com.skybook.domain.AuditLog;
import com.skybook.domain.ExportType;
import com.skybook.domain.User;
import com.skybook.dto.audit.AuditExportResponse;
import com.skybook.dto.audit.AuditLogResponse;
import com.skybook.exception.ResourceNotFoundException;
import com.skybook.repository.AuditExportRepository;
import com.skybook.repository.AuditLogRepository;
import com.skybook.repository.UserRepository;
import com.skybook.security.UserPrincipal;
import com.skybook.util.IdUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAuditService {

    private final AuditLogRepository auditLogRepository;
    private final AuditExportRepository auditExportRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> search(
            String username,
            String action,
            LocalDate dateFrom,
            LocalDate dateTo,
            int page,
            int size
    ) {
        Instant from = (dateFrom != null ? dateFrom : LocalDate.now(ZoneOffset.UTC).minusDays(30))
                .atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant to = (dateTo != null ? dateTo.plusDays(1) : LocalDate.now(ZoneOffset.UTC).plusDays(1))
                .atStartOfDay().toInstant(ZoneOffset.UTC);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 200));
        return auditLogRepository.search(blankToNull(username), blankToNull(action), from, to, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public byte[] exportCsv(
            String username,
            String action,
            LocalDate dateFrom,
            LocalDate dateTo,
            UserPrincipal principal,
            HttpServletRequest request
    ) {
        List<AuditLog> logs = loadAll(username, action, dateFrom, dateTo);
        StringBuilder sb = new StringBuilder();
        sb.append("auditId,timestamp,username,userId,role,ipAddress,sessionId,action,resource,httpMethod,responseStatus,browser,operatingSystem,executionTimeMs\n");
        for (AuditLog a : logs) {
            sb.append(csv(a.getId())).append(',')
                    .append(csv(a.getTimestamp())).append(',')
                    .append(csv(a.getUsername())).append(',')
                    .append(csv(a.getUserId())).append(',')
                    .append(csv(a.getRole())).append(',')
                    .append(csv(a.getIpAddress())).append(',')
                    .append(csv(a.getSessionId())).append(',')
                    .append(csv(a.getAction())).append(',')
                    .append(csv(a.getResource())).append(',')
                    .append(csv(a.getHttpMethod())).append(',')
                    .append(a.getResponseStatus() == null ? "" : a.getResponseStatus()).append(',')
                    .append(csv(a.getBrowser())).append(',')
                    .append(csv(a.getOperatingSystem())).append(',')
                    .append(a.getExecutionTimeMs() == null ? "" : a.getExecutionTimeMs())
                    .append('\n');
        }
        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        persistExportMeta(principal, ExportType.CSV, dateFrom, dateTo, username, action, "audit_export.csv", logs.size(), request);
        return bytes;
    }

    @Transactional
    public byte[] exportExcel(
            String username,
            String action,
            LocalDate dateFrom,
            LocalDate dateTo,
            UserPrincipal principal,
            HttpServletRequest request
    ) {
        List<AuditLog> logs = loadAll(username, action, dateFrom, dateTo);
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("AuditLogs");
            Row header = sheet.createRow(0);
            String[] cols = {
                    "auditId", "timestamp", "username", "userId", "role", "ipAddress", "sessionId",
                    "action", "resource", "httpMethod", "responseStatus", "browser", "operatingSystem", "executionTimeMs"
            };
            for (int i = 0; i < cols.length; i++) {
                header.createCell(i).setCellValue(cols[i]);
            }
            int rowIdx = 1;
            for (AuditLog a : logs) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(nullToEmpty(a.getId()));
                row.createCell(1).setCellValue(a.getTimestamp() == null ? "" : a.getTimestamp().toString());
                row.createCell(2).setCellValue(nullToEmpty(a.getUsername()));
                row.createCell(3).setCellValue(nullToEmpty(a.getUserId()));
                row.createCell(4).setCellValue(nullToEmpty(a.getRole()));
                row.createCell(5).setCellValue(nullToEmpty(a.getIpAddress()));
                row.createCell(6).setCellValue(nullToEmpty(a.getSessionId()));
                row.createCell(7).setCellValue(nullToEmpty(a.getAction()));
                row.createCell(8).setCellValue(nullToEmpty(a.getResource()));
                row.createCell(9).setCellValue(nullToEmpty(a.getHttpMethod()));
                row.createCell(10).setCellValue(a.getResponseStatus() == null ? "" : a.getResponseStatus().toString());
                row.createCell(11).setCellValue(nullToEmpty(a.getBrowser()));
                row.createCell(12).setCellValue(nullToEmpty(a.getOperatingSystem()));
                row.createCell(13).setCellValue(a.getExecutionTimeMs() == null ? "" : a.getExecutionTimeMs().toString());
            }
            workbook.write(out);
            byte[] bytes = out.toByteArray();
            persistExportMeta(principal, ExportType.EXCEL, dateFrom, dateTo, username, action, "audit_export.xlsx", logs.size(), request);
            return bytes;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create Excel export", e);
        }
    }

    @Transactional
    public byte[] exportMonthly(YearMonth month, UserPrincipal principal, HttpServletRequest request) {
        YearMonth ym = month != null ? month : YearMonth.now(ZoneOffset.UTC).minusMonths(1);
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();
        byte[] bytes = exportExcel(null, null, from, to, principal, request);
        // overwrite last export type marker
        List<AuditExport> recent = auditExportRepository.findAllByOrderByCreatedAtDesc();
        if (!recent.isEmpty()) {
            AuditExport last = recent.getFirst();
            last.setExportType(ExportType.MONTHLY);
            last.setFileName("audit_monthly_" + ym + ".xlsx");
            auditExportRepository.save(last);
        }
        return bytes;
    }

    @Transactional(readOnly = true)
    public List<AuditExportResponse> listExports() {
        return auditExportRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(e -> AuditExportResponse.builder()
                        .id(e.getId())
                        .exportType(e.getExportType().name())
                        .dateFrom(e.getDateFrom())
                        .dateTo(e.getDateTo())
                        .filterUsername(e.getFilterUsername())
                        .filterAction(e.getFilterAction())
                        .fileName(e.getFileName())
                        .rowCount(e.getRowCount())
                        .createdAt(e.getCreatedAt())
                        .requestedByUsername(e.getRequestedBy().getUsername())
                        .build())
                .toList();
    }

    @Transactional
    public void recordAiQuery(UserPrincipal principal, String details, HttpServletRequest request) {
        auditService.record(
                AuditActions.AI_QUERY,
                "/api/v1/admin/audit/ai-query",
                "POST",
                200,
                0,
                details,
                request
        );
    }

    private List<AuditLog> loadAll(String username, String action, LocalDate dateFrom, LocalDate dateTo) {
        Instant from = (dateFrom != null ? dateFrom : LocalDate.now(ZoneOffset.UTC).minusDays(30))
                .atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant to = (dateTo != null ? dateTo.plusDays(1) : LocalDate.now(ZoneOffset.UTC).plusDays(1))
                .atStartOfDay().toInstant(ZoneOffset.UTC);
        return auditLogRepository.searchAll(blankToNull(username), blankToNull(action), from, to);
    }

    private void persistExportMeta(
            UserPrincipal principal,
            ExportType type,
            LocalDate dateFrom,
            LocalDate dateTo,
            String username,
            String action,
            String fileName,
            int rowCount,
            HttpServletRequest request
    ) {
        long start = System.currentTimeMillis();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        AuditExport export = AuditExport.builder()
                .id(IdUtils.uuid())
                .requestedBy(user)
                .exportType(type)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .filterUsername(username)
                .filterAction(action)
                .fileName(fileName)
                .rowCount(rowCount)
                .createdAt(Instant.now())
                .build();
        auditExportRepository.save(export);
        auditService.record(
                AuditActions.EXPORT_REQUEST,
                "/api/v1/admin/audit/export/" + type.name().toLowerCase(),
                "GET",
                200,
                (int) (System.currentTimeMillis() - start),
                "{\"format\":\"" + type + "\",\"rowCount\":" + rowCount + "}",
                request
        );
    }

    private AuditLogResponse toResponse(AuditLog a) {
        return AuditLogResponse.builder()
                .auditId(a.getId())
                .timestamp(a.getTimestamp())
                .username(a.getUsername())
                .userId(a.getUserId())
                .role(a.getRole())
                .ipAddress(a.getIpAddress())
                .sessionId(a.getSessionId())
                .action(a.getAction())
                .resource(a.getResource())
                .httpMethod(a.getHttpMethod())
                .responseStatus(a.getResponseStatus())
                .browser(a.getBrowser())
                .operatingSystem(a.getOperatingSystem())
                .executionTimeMs(a.getExecutionTimeMs())
                .details(a.getDetails())
                .build();
    }

    private String csv(Object value) {
        if (value == null) {
            return "";
        }
        String s = value.toString().replace("\"", "\"\"");
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s + "\"";
        }
        return s;
    }

    private String nullToEmpty(String v) {
        return v == null ? "" : v;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

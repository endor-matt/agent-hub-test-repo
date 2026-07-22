package com.skybook.controller;

import com.skybook.dto.audit.AuditExportResponse;
import com.skybook.dto.audit.AuditLogResponse;
import com.skybook.dto.common.MessageResponse;
import com.skybook.dto.user.UserResponse;
import com.skybook.security.UserPrincipal;
import com.skybook.service.AdminAuditService;
import com.skybook.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin")
public class AdminController {

    private final AdminAuditService adminAuditService;
    private final UserService userService;

    @GetMapping("/users")
    @Operation(summary = "List users")
    public List<UserResponse> users() {
        return userService.listUsers();
    }

    @GetMapping("/audit")
    @Operation(summary = "Search audit logs")
    public Page<AuditLogResponse> audit(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return adminAuditService.search(username, action, dateFrom, dateTo, page, size);
    }

    @GetMapping("/audit/export/csv")
    @Operation(summary = "Export audit logs as CSV")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request
    ) {
        byte[] body = adminAuditService.exportCsv(username, action, dateFrom, dateTo, principal, request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=audit_export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(body);
    }

    @GetMapping("/audit/export/excel")
    @Operation(summary = "Export audit logs as Excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request
    ) {
        byte[] body = adminAuditService.exportExcel(username, action, dateFrom, dateTo, principal, request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=audit_export.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(body);
    }

    @GetMapping("/audit/export/monthly")
    @Operation(summary = "Export monthly audit logs")
    public ResponseEntity<byte[]> exportMonthly(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request
    ) {
        byte[] body = adminAuditService.exportMonthly(month, principal, request);
        String name = "audit_monthly_" + (month != null ? month : YearMonth.now().minusMonths(1)) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(body);
    }

    @GetMapping("/audit/exports")
    @Operation(summary = "List previous audit exports")
    public List<AuditExportResponse> exports() {
        return adminAuditService.listExports();
    }

    @PostMapping("/audit/ai-query")
    @Operation(summary = "Record AI_QUERY audit event (called by AI service / frontend bridge)")
    public MessageResponse aiQuery(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request
    ) {
        adminAuditService.recordAiQuery(principal, body == null ? "{}" : body.toString(), request);
        return MessageResponse.builder().message("AI query audited").build();
    }
}

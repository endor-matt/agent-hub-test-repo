package com.skybook.service;

import com.skybook.domain.AuditLog;
import com.skybook.domain.User;
import com.skybook.repository.AuditLogRepository;
import com.skybook.security.UserPrincipal;
import com.skybook.util.IdUtils;
import com.skybook.util.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void record(
            String action,
            String resource,
            String httpMethod,
            Integer responseStatus,
            Integer executionTimeMs,
            String detailsJson,
            HttpServletRequest request
    ) {
        String username = null;
        String userId = null;
        String role = null;
        String sessionId = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            username = principal.getUsername();
            userId = principal.getId();
            role = principal.getRole();
        }

        if (request != null) {
            Object jti = request.getAttribute("jwtJti");
            if (jti != null) {
                sessionId = jti.toString();
            }
        }

        String userAgent = request != null ? request.getHeader("User-Agent") : null;

        AuditLog log = AuditLog.builder()
                .id(IdUtils.uuid())
                .timestamp(Instant.now())
                .username(username)
                .userId(userId)
                .role(role)
                .ipAddress(request != null ? RequestUtils.clientIp(request) : null)
                .sessionId(sessionId)
                .action(action)
                .resource(resource)
                .httpMethod(httpMethod)
                .responseStatus(responseStatus)
                .browser(RequestUtils.browser(userAgent))
                .operatingSystem(RequestUtils.operatingSystem(userAgent))
                .executionTimeMs(executionTimeMs)
                .details(detailsJson)
                .build();

        auditLogRepository.save(log);
    }

    @Transactional
    public void recordForUser(
            User user,
            String action,
            String resource,
            String httpMethod,
            Integer responseStatus,
            Integer executionTimeMs,
            String detailsJson,
            HttpServletRequest request,
            String sessionId
    ) {
        String userAgent = request != null ? request.getHeader("User-Agent") : null;
        AuditLog log = AuditLog.builder()
                .id(IdUtils.uuid())
                .timestamp(Instant.now())
                .username(user.getUsername())
                .userId(user.getId())
                .role(user.getRole().getName())
                .ipAddress(request != null ? RequestUtils.clientIp(request) : null)
                .sessionId(sessionId)
                .action(action)
                .resource(resource)
                .httpMethod(httpMethod)
                .responseStatus(responseStatus)
                .browser(RequestUtils.browser(userAgent))
                .operatingSystem(RequestUtils.operatingSystem(userAgent))
                .executionTimeMs(executionTimeMs)
                .details(detailsJson)
                .build();
        auditLogRepository.save(log);
    }
}

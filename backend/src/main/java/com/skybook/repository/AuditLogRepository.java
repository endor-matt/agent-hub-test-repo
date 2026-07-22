package com.skybook.repository;

import com.skybook.domain.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, String> {

    @Query("""
        SELECT a FROM AuditLog a
        WHERE (:username IS NULL OR LOWER(a.username) = LOWER(:username))
          AND (:action IS NULL OR a.action = :action)
          AND a.timestamp >= :from
          AND a.timestamp < :to
        ORDER BY a.timestamp DESC
        """)
    Page<AuditLog> search(
            @Param("username") String username,
            @Param("action") String action,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable
    );

    @Query("""
        SELECT a FROM AuditLog a
        WHERE (:username IS NULL OR LOWER(a.username) = LOWER(:username))
          AND (:action IS NULL OR a.action = :action)
          AND a.timestamp >= :from
          AND a.timestamp < :to
        ORDER BY a.timestamp DESC
        """)
    List<AuditLog> searchAll(
            @Param("username") String username,
            @Param("action") String action,
            @Param("from") Instant from,
            @Param("to") Instant to
    );
}

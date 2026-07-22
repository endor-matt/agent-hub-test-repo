package com.skybook.repository;

import com.skybook.domain.AuditExport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditExportRepository extends JpaRepository<AuditExport, String> {
    List<AuditExport> findAllByOrderByCreatedAtDesc();
}

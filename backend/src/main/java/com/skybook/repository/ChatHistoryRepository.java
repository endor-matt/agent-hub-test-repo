package com.skybook.repository;

import com.skybook.domain.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, String> {
    List<ChatHistory> findBySessionIdOrderByCreatedAtAsc(String sessionId);
}

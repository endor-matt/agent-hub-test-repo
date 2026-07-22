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

import java.time.Instant;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(nullable = false, unique = true, length = 32)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}

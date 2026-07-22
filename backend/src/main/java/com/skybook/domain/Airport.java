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

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "airports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Airport {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(name = "iata_code", nullable = false, unique = true, length = 3)
    private String iataCode;

    @Column(name = "icao_code", length = 4)
    private String icaoCode;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = false, length = 64)
    private String timezone;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}

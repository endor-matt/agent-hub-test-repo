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
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "flights")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flight {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(name = "flight_number", nullable = false, length = 16)
    private String flightNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "airline_id", nullable = false)
    private Airline airline;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_airport_id", nullable = false)
    private Airport sourceAirport;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dest_airport_id", nullable = false)
    private Airport destAirport;

    @Column(name = "departure_time", nullable = false)
    private Instant departureTime;

    @Column(name = "arrival_time", nullable = false)
    private Instant arrivalTime;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "aircraft_type", length = 64)
    private String aircraftType;

    @Enumerated(EnumType.STRING)
    @Column(name = "cabin_class", nullable = false, length = 32)
    private CabinClass cabinClass;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private FlightStatus status;

    @Column(name = "baggage_allowance_kg", nullable = false)
    private Integer baggageAllowanceKg;

    @Version
    private Integer version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}

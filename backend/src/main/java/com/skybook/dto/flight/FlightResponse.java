package com.skybook.dto.flight;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class FlightResponse {
    private String id;
    private String flightNumber;
    private String airlineCode;
    private String airlineName;
    private String sourceIata;
    private String sourceCity;
    private String destIata;
    private String destCity;
    private Instant departureTime;
    private Instant arrivalTime;
    private Integer durationMinutes;
    private String aircraftType;
    private String cabinClass;
    private BigDecimal basePrice;
    private String currency;
    private Integer availableSeats;
    private Integer totalSeats;
    private String status;
    private Integer baggageAllowanceKg;
}

package com.skybook.dto.booking;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class BookingResponse {
    private String id;
    private String bookingReference;
    private String status;
    private String flightId;
    private String flightNumber;
    private String airlineName;
    private String sourceIata;
    private String destIata;
    private Instant departureTime;
    private Instant arrivalTime;
    private Integer passengerCount;
    private List<String> seats;
    private String passengersJson;
    private BigDecimal totalAmount;
    private String currency;
    private String contactEmail;
    private String contactPhone;
    private Instant createdAt;
    private Instant cancelledAt;
    private String cancellationReason;
}

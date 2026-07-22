package com.skybook.service;

import com.skybook.domain.User;
import com.skybook.dto.flight.FlightResponse;
import com.skybook.dto.booking.BookingResponse;
import com.skybook.dto.user.UserResponse;
import com.skybook.domain.Booking;
import com.skybook.domain.Flight;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MapperService {

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole().getName())
                .status(user.getStatus().name())
                .build();
    }

    public FlightResponse toFlightResponse(Flight f) {
        return FlightResponse.builder()
                .id(f.getId())
                .flightNumber(f.getFlightNumber())
                .airlineCode(f.getAirline().getCode())
                .airlineName(f.getAirline().getName())
                .sourceIata(f.getSourceAirport().getIataCode())
                .sourceCity(f.getSourceAirport().getCity())
                .destIata(f.getDestAirport().getIataCode())
                .destCity(f.getDestAirport().getCity())
                .departureTime(f.getDepartureTime())
                .arrivalTime(f.getArrivalTime())
                .durationMinutes(f.getDurationMinutes())
                .aircraftType(f.getAircraftType())
                .cabinClass(f.getCabinClass().name())
                .basePrice(f.getBasePrice())
                .currency(f.getCurrency())
                .availableSeats(f.getAvailableSeats())
                .totalSeats(f.getTotalSeats())
                .status(f.getStatus().name())
                .baggageAllowanceKg(f.getBaggageAllowanceKg())
                .build();
    }

    public BookingResponse toBookingResponse(Booking b) {
        List<String> seats = b.getSeats() == null || b.getSeats().isBlank()
                ? List.of()
                : Arrays.asList(b.getSeats().split(","));
        return BookingResponse.builder()
                .id(b.getId())
                .bookingReference(b.getBookingReference())
                .status(b.getStatus().name())
                .flightId(b.getFlight().getId())
                .flightNumber(b.getFlight().getFlightNumber())
                .airlineName(b.getFlight().getAirline().getName())
                .sourceIata(b.getFlight().getSourceAirport().getIataCode())
                .destIata(b.getFlight().getDestAirport().getIataCode())
                .departureTime(b.getFlight().getDepartureTime())
                .arrivalTime(b.getFlight().getArrivalTime())
                .passengerCount(b.getPassengerCount())
                .seats(seats.stream().map(String::trim).toList())
                .passengersJson(b.getPassengersJson())
                .totalAmount(b.getTotalAmount())
                .currency(b.getCurrency())
                .contactEmail(b.getContactEmail())
                .contactPhone(b.getContactPhone())
                .createdAt(b.getCreatedAt())
                .cancelledAt(b.getCancelledAt())
                .cancellationReason(b.getCancellationReason())
                .build();
    }
}

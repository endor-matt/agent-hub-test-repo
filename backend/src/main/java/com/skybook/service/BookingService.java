package com.skybook.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skybook.domain.AuditActions;
import com.skybook.domain.Booking;
import com.skybook.domain.BookingStatus;
import com.skybook.domain.Flight;
import com.skybook.domain.FlightStatus;
import com.skybook.domain.User;
import com.skybook.dto.booking.CancelBookingRequest;
import com.skybook.dto.booking.CreateBookingRequest;
import com.skybook.dto.booking.BookingResponse;
import com.skybook.dto.booking.PassengerRequest;
import com.skybook.exception.BadRequestException;
import com.skybook.exception.ForbiddenException;
import com.skybook.exception.ResourceNotFoundException;
import com.skybook.repository.BookingRepository;
import com.skybook.repository.FlightRepository;
import com.skybook.repository.UserRepository;
import com.skybook.security.UserPrincipal;
import com.skybook.util.IdUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final MapperService mapperService;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @Transactional
    public BookingResponse create(CreateBookingRequest request, UserPrincipal principal, HttpServletRequest httpRequest) {
        long start = System.currentTimeMillis();
        if (request.getPassengers().size() != request.getSeats().size()) {
            throw new BadRequestException("Passenger count must match seat count");
        }
        Set<String> uniqueSeats = new HashSet<>();
        for (String seat : request.getSeats()) {
            String normalized = seat.trim().toUpperCase(Locale.ROOT);
            if (!uniqueSeats.add(normalized)) {
                throw new BadRequestException("Duplicate seat selection: " + normalized);
            }
        }

        Flight flight = flightRepository.findById(request.getFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found"));
        if (flight.getStatus() != FlightStatus.SCHEDULED) {
            throw new BadRequestException("Flight is not available for booking");
        }
        if (flight.getAvailableSeats() < request.getSeats().size()) {
            throw new BadRequestException("Not enough seats available");
        }

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Map<String, Object>> passengersPayload = new ArrayList<>();
        for (int i = 0; i < request.getPassengers().size(); i++) {
            PassengerRequest p = request.getPassengers().get(i);
            Map<String, Object> row = new HashMap<>();
            row.put("firstName", p.getFirstName());
            row.put("lastName", p.getLastName());
            row.put("dob", p.getDob());
            row.put("passport", p.getPassport());
            row.put("seat", request.getSeats().get(i).trim().toUpperCase(Locale.ROOT));
            passengersPayload.add(row);
        }

        String passengersJson;
        try {
            passengersJson = objectMapper.writeValueAsString(passengersPayload);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid passenger payload");
        }

        Instant now = Instant.now();
        BigDecimal total = flight.getBasePrice().multiply(BigDecimal.valueOf(request.getSeats().size()));
        String reference = generateUniqueReference();

        Booking booking = Booking.builder()
                .id(IdUtils.uuid())
                .bookingReference(reference)
                .user(user)
                .flight(flight)
                .status(BookingStatus.CONFIRMED)
                .passengerCount(request.getPassengers().size())
                .passengersJson(passengersJson)
                .seats(String.join(",", uniqueSeats))
                .totalAmount(total)
                .currency(flight.getCurrency())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .createdAt(now)
                .updatedAt(now)
                .build();

        flight.setAvailableSeats(flight.getAvailableSeats() - request.getSeats().size());
        flight.setUpdatedAt(now);
        flightRepository.save(flight);
        bookingRepository.save(booking);

        auditService.record(
                AuditActions.BOOKING_CREATED,
                "/api/v1/bookings",
                "POST",
                201,
                (int) (System.currentTimeMillis() - start),
                "{\"bookingReference\":\"" + reference + "\"}",
                httpRequest
        );

        return mapperService.toBookingResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> myBookings(UserPrincipal principal) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(principal.getId()).stream()
                .map(mapperService::toBookingResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse getById(String id, UserPrincipal principal) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        assertOwnerOrAdmin(booking, principal);
        return mapperService.toBookingResponse(booking);
    }

    @Transactional
    public BookingResponse cancel(String id, CancelBookingRequest request, UserPrincipal principal, HttpServletRequest httpRequest) {
        long start = System.currentTimeMillis();
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        assertOwnerOrAdmin(booking, principal);
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking already cancelled");
        }

        Instant now = Instant.now();
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(now);
        booking.setCancellationReason(request != null ? request.getReason() : null);
        booking.setUpdatedAt(now);

        Flight flight = booking.getFlight();
        flight.setAvailableSeats(flight.getAvailableSeats() + booking.getPassengerCount());
        flight.setUpdatedAt(now);

        bookingRepository.save(booking);
        flightRepository.save(flight);

        auditService.record(
                AuditActions.BOOKING_CANCELLED,
                "/api/v1/bookings/" + id + "/cancel",
                "POST",
                200,
                (int) (System.currentTimeMillis() - start),
                "{\"bookingReference\":\"" + booking.getBookingReference() + "\"}",
                httpRequest
        );

        return mapperService.toBookingResponse(booking);
    }

    private void assertOwnerOrAdmin(Booking booking, UserPrincipal principal) {
        boolean admin = "ADMIN".equals(principal.getRole());
        boolean owner = booking.getUser().getId().equals(principal.getId());
        if (!admin && !owner) {
            throw new ForbiddenException("You cannot access this booking");
        }
    }

    private String generateUniqueReference() {
        for (int i = 0; i < 10; i++) {
            String ref = IdUtils.bookingReference();
            if (!bookingRepository.existsByBookingReference(ref)) {
                return ref;
            }
        }
        throw new IllegalStateException("Unable to generate booking reference");
    }
}

package com.skybook.controller;

import com.skybook.dto.booking.BookingResponse;
import com.skybook.dto.booking.CancelBookingRequest;
import com.skybook.dto.booking.CreateBookingRequest;
import com.skybook.security.UserPrincipal;
import com.skybook.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a booking")
    public BookingResponse create(
            @Valid @RequestBody CreateBookingRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest httpRequest
    ) {
        return bookingService.create(request, principal, httpRequest);
    }

    @GetMapping("/me")
    @Operation(summary = "List current user bookings")
    public List<BookingResponse> mine(@AuthenticationPrincipal UserPrincipal principal) {
        return bookingService.myBookings(principal);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID")
    public BookingResponse get(@PathVariable String id, @AuthenticationPrincipal UserPrincipal principal) {
        return bookingService.getById(id, principal);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a booking")
    public BookingResponse cancel(
            @PathVariable String id,
            @RequestBody(required = false) CancelBookingRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest httpRequest
    ) {
        return bookingService.cancel(id, request != null ? request : new CancelBookingRequest(), principal, httpRequest);
    }
}

package com.skybook.controller;

import com.skybook.dto.common.AirlineResponse;
import com.skybook.dto.common.AirportResponse;
import com.skybook.dto.flight.FlightResponse;
import com.skybook.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Flights")
public class FlightController {

    private final FlightService flightService;

    @GetMapping("/flights/search")
    @Operation(summary = "Search flights with filters")
    public List<FlightResponse> search(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String airline,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return flightService.search(source, destination, date, airline, minPrice, maxPrice);
    }

    @GetMapping("/flights/{id}")
    @Operation(summary = "Get flight by ID")
    public FlightResponse get(@PathVariable String id) {
        return flightService.getById(id);
    }

    @GetMapping("/airlines")
    @Operation(summary = "List airlines")
    public List<AirlineResponse> airlines() {
        return flightService.listAirlines();
    }

    @GetMapping("/airports")
    @Operation(summary = "List airports")
    public List<AirportResponse> airports() {
        return flightService.listAirports();
    }
}

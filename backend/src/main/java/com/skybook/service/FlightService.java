package com.skybook.service;

import com.skybook.domain.Airline;
import com.skybook.domain.Airport;
import com.skybook.domain.Flight;
import com.skybook.domain.FlightStatus;
import com.skybook.dto.common.AirlineResponse;
import com.skybook.dto.common.AirportResponse;
import com.skybook.dto.flight.FlightResponse;
import com.skybook.exception.ResourceNotFoundException;
import com.skybook.repository.AirlineRepository;
import com.skybook.repository.AirportRepository;
import com.skybook.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;
    private final MapperService mapperService;

    @Transactional(readOnly = true)
    public List<FlightResponse> search(
            String source,
            String destination,
            LocalDate date,
            String airline,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {
        LocalDate searchDate = date != null ? date : LocalDate.now(ZoneOffset.UTC);
        Instant from = searchDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant to = searchDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        return flightRepository.search(
                        blankToNull(source),
                        blankToNull(destination),
                        blankToNull(airline),
                        minPrice,
                        maxPrice,
                        from,
                        to,
                        FlightStatus.SCHEDULED
                ).stream()
                .map(mapperService::toFlightResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FlightResponse getById(String id) {
        return mapperService.toFlightResponse(requireFlight(id));
    }

    @Transactional(readOnly = true)
    public Flight requireFlight(String id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<AirlineResponse> listAirlines() {
        return airlineRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(this::toAirline)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AirportResponse> listAirports() {
        return airportRepository.findByActiveTrueOrderByCityAsc().stream()
                .map(this::toAirport)
                .toList();
    }

    private AirlineResponse toAirline(Airline a) {
        return AirlineResponse.builder()
                .id(a.getId())
                .code(a.getCode())
                .name(a.getName())
                .country(a.getCountry())
                .build();
    }

    private AirportResponse toAirport(Airport a) {
        return AirportResponse.builder()
                .id(a.getId())
                .iataCode(a.getIataCode())
                .name(a.getName())
                .city(a.getCity())
                .country(a.getCountry())
                .timezone(a.getTimezone())
                .build();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

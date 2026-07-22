package com.skybook.service;

import com.skybook.domain.Flight;
import com.skybook.domain.FlightStatus;
import com.skybook.domain.Airline;
import com.skybook.domain.Airport;
import com.skybook.domain.CabinClass;
import com.skybook.repository.AirlineRepository;
import com.skybook.repository.AirportRepository;
import com.skybook.repository.FlightRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;
    @Mock
    private AirlineRepository airlineRepository;
    @Mock
    private AirportRepository airportRepository;
    @Spy
    private MapperService mapperService = new MapperService();

    @InjectMocks
    private FlightService flightService;

    @Test
    void searchMapsResults() {
        Airline airline = Airline.builder().id("a1").code("SA").name("SkyBook Airways").country("US").active(true).createdAt(Instant.now()).build();
        Airport jfk = Airport.builder().id("ap1").iataCode("JFK").name("JFK").city("New York").country("US").timezone("UTC").active(true).createdAt(Instant.now()).build();
        Airport lax = Airport.builder().id("ap2").iataCode("LAX").name("LAX").city("LA").country("US").timezone("UTC").active(true).createdAt(Instant.now()).build();
        Flight flight = Flight.builder()
                .id("f1")
                .flightNumber("SA101")
                .airline(airline)
                .sourceAirport(jfk)
                .destAirport(lax)
                .departureTime(Instant.parse("2026-07-20T08:00:00Z"))
                .arrivalTime(Instant.parse("2026-07-20T11:30:00Z"))
                .durationMinutes(210)
                .cabinClass(CabinClass.ECONOMY)
                .basePrice(new BigDecimal("249.00"))
                .currency("USD")
                .totalSeats(180)
                .availableSeats(100)
                .status(FlightStatus.SCHEDULED)
                .baggageAllowanceKg(23)
                .version(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(flightRepository.search(eq("JFK"), eq("LAX"), isNull(), isNull(), isNull(), any(), any(), eq(FlightStatus.SCHEDULED)))
                .thenReturn(List.of(flight));

        var results = flightService.search("JFK", "LAX", LocalDate.parse("2026-07-20"), null, null, null);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getFlightNumber()).isEqualTo("SA101");
        assertThat(results.getFirst().getSourceIata()).isEqualTo("JFK");
        assertThat(results.getFirst().getDestIata()).isEqualTo("LAX");
    }
}

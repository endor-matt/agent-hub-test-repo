package com.skybook.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skybook.domain.Airline;
import com.skybook.domain.Airport;
import com.skybook.domain.Booking;
import com.skybook.domain.BookingStatus;
import com.skybook.domain.CabinClass;
import com.skybook.domain.Flight;
import com.skybook.domain.FlightStatus;
import com.skybook.domain.Role;
import com.skybook.domain.User;
import com.skybook.domain.UserStatus;
import com.skybook.dto.booking.CancelBookingRequest;
import com.skybook.dto.booking.CreateBookingRequest;
import com.skybook.dto.booking.PassengerRequest;
import com.skybook.exception.BadRequestException;
import com.skybook.exception.ForbiddenException;
import com.skybook.repository.BookingRepository;
import com.skybook.repository.FlightRepository;
import com.skybook.repository.UserRepository;
import com.skybook.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private FlightRepository flightRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuditService auditService;
    @Spy
    private MapperService mapperService = new MapperService();
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private BookingService bookingService;

    private User customer;
    private UserPrincipal customerPrincipal;
    private Flight flight;

    @BeforeEach
    void setUp() {
        Role role = Role.builder().id("r1").name("CUSTOMER").build();
        customer = User.builder()
                .id("u1")
                .username("jdoe")
                .email("jane@example.com")
                .passwordHash("hash")
                .firstName("Jane")
                .lastName("Doe")
                .role(role)
                .status(UserStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        customerPrincipal = new UserPrincipal(customer);

        Airline airline = Airline.builder().id("a1").code("SA").name("SkyBook Airways").country("US").active(true).createdAt(Instant.now()).build();
        Airport jfk = Airport.builder().id("ap1").iataCode("JFK").name("JFK").city("New York").country("US").timezone("UTC").active(true).createdAt(Instant.now()).build();
        Airport lax = Airport.builder().id("ap2").iataCode("LAX").name("LAX").city("LA").country("US").timezone("UTC").active(true).createdAt(Instant.now()).build();

        flight = Flight.builder()
                .id("f1")
                .flightNumber("SA101")
                .airline(airline)
                .sourceAirport(jfk)
                .destAirport(lax)
                .departureTime(Instant.now().plusSeconds(86400))
                .arrivalTime(Instant.now().plusSeconds(90000))
                .durationMinutes(210)
                .cabinClass(CabinClass.ECONOMY)
                .basePrice(new BigDecimal("249.00"))
                .currency("USD")
                .totalSeats(180)
                .availableSeats(50)
                .status(FlightStatus.SCHEDULED)
                .baggageAllowanceKg(23)
                .version(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void createBookingDecrementsSeats() {
        when(flightRepository.findById("f1")).thenReturn(Optional.of(flight));
        when(userRepository.findById("u1")).thenReturn(Optional.of(customer));
        when(bookingRepository.existsByBookingReference(anyString())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));
        when(flightRepository.save(any(Flight.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateBookingRequest req = new CreateBookingRequest();
        req.setFlightId("f1");
        req.setSeats(List.of("12A"));
        PassengerRequest p = new PassengerRequest();
        p.setFirstName("Jane");
        p.setLastName("Doe");
        req.setPassengers(List.of(p));
        req.setContactEmail("jane@example.com");

        var response = bookingService.create(req, customerPrincipal, mock(HttpServletRequest.class));

        assertThat(response.getStatus()).isEqualTo("CONFIRMED");
        assertThat(response.getTotalAmount()).isEqualByComparingTo("249.00");
        assertThat(flight.getAvailableSeats()).isEqualTo(49);
        verify(auditService).record(anyString(), anyString(), anyString(), any(), any(), anyString(), any());
    }

    @Test
    void createBookingRejectsMismatchedPassengerAndSeatCounts() {
        CreateBookingRequest req = new CreateBookingRequest();
        req.setFlightId("f1");
        req.setSeats(List.of("12A", "12B"));
        PassengerRequest p = new PassengerRequest();
        p.setFirstName("Jane");
        p.setLastName("Doe");
        req.setPassengers(List.of(p));
        req.setContactEmail("jane@example.com");

        assertThatThrownBy(() -> bookingService.create(req, customerPrincipal, mock(HttpServletRequest.class)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Passenger count");
    }

    @Test
    void cancelRestoresSeatsAndForbidsOtherUsers() {
        Booking booking = Booking.builder()
                .id("b1")
                .bookingReference("SBK12345")
                .user(customer)
                .flight(flight)
                .status(BookingStatus.CONFIRMED)
                .passengerCount(1)
                .passengersJson("[]")
                .seats("12A")
                .totalAmount(new BigDecimal("249.00"))
                .currency("USD")
                .contactEmail("jane@example.com")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Role otherRole = Role.builder().id("r1").name("CUSTOMER").build();
        User other = User.builder()
                .id("u2")
                .username("asmith")
                .email("a@example.com")
                .passwordHash("hash")
                .firstName("Alex")
                .lastName("Smith")
                .role(otherRole)
                .status(UserStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        UserPrincipal otherPrincipal = new UserPrincipal(other);

        when(bookingRepository.findById("b1")).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.cancel("b1", new CancelBookingRequest(), otherPrincipal, mock(HttpServletRequest.class)))
                .isInstanceOf(ForbiddenException.class);

        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));
        when(flightRepository.save(any(Flight.class))).thenAnswer(inv -> inv.getArgument(0));

        int before = flight.getAvailableSeats();
        bookingService.cancel("b1", new CancelBookingRequest(), customerPrincipal, mock(HttpServletRequest.class));

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(flight.getAvailableSeats()).isEqualTo(before + 1);
    }
}

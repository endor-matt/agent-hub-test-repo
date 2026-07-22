package com.skybook.repository;

import com.skybook.domain.Flight;
import com.skybook.domain.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, String> {

    @Query("""
        SELECT f FROM Flight f
        WHERE f.status = :status
          AND (:sourceIata IS NULL OR UPPER(f.sourceAirport.iataCode) = UPPER(:sourceIata))
          AND (:destIata IS NULL OR UPPER(f.destAirport.iataCode) = UPPER(:destIata))
          AND (:airlineCode IS NULL OR UPPER(f.airline.code) = UPPER(:airlineCode))
          AND (:minPrice IS NULL OR f.basePrice >= :minPrice)
          AND (:maxPrice IS NULL OR f.basePrice <= :maxPrice)
          AND f.departureTime >= :from
          AND f.departureTime < :to
        ORDER BY f.departureTime ASC, f.basePrice ASC
        """)
    List<Flight> search(
            @Param("sourceIata") String sourceIata,
            @Param("destIata") String destIata,
            @Param("airlineCode") String airlineCode,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("status") FlightStatus status
    );
}

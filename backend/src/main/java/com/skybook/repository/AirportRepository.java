package com.skybook.repository;

import com.skybook.domain.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AirportRepository extends JpaRepository<Airport, String> {
    List<Airport> findByActiveTrueOrderByCityAsc();
    Optional<Airport> findByIataCode(String iataCode);
}

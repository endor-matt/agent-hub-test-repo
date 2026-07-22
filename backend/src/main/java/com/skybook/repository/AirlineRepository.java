package com.skybook.repository;

import com.skybook.domain.Airline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AirlineRepository extends JpaRepository<Airline, String> {
    List<Airline> findByActiveTrueOrderByNameAsc();
    Optional<Airline> findByCode(String code);
}

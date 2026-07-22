package com.skybook.repository;

import com.skybook.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, String> {
    List<Booking> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<Booking> findByBookingReference(String bookingReference);
    boolean existsByBookingReference(String bookingReference);
}

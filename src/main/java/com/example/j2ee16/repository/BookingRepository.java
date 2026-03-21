package com.example.j2ee16.repository;

import com.example.j2ee16.entity.Booking;
import com.example.j2ee16.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingCode(String bookingCode);
    List<Booking> findByBookingStatusAndHoldExpiresAtBefore(BookingStatus status, Instant time);
}

package com.example.j2ee16.repository;

import com.example.j2ee16.entity.HoldStatus;
import com.example.j2ee16.entity.SeatHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SeatHoldRepository extends JpaRepository<SeatHold, Long> {
    List<SeatHold> findByTripIdAndHoldStatusAndExpiresAtAfter(Long tripId, HoldStatus status, Instant now);

    List<SeatHold> findByBookingId(Long bookingId);
}

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
    List<Booking> findByBookingStatusInAndHoldExpiresAtBefore(List<BookingStatus> statuses, Instant time);
    List<Booking> findByUserEmail(String email);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.bookingStatus IN :statuses AND b.createdAt BETWEEN :start AND :end")
    java.math.BigDecimal sumRevenueByStatusInAndDateBetween(
            @org.springframework.data.repository.query.Param("statuses") List<BookingStatus> statuses,
            @org.springframework.data.repository.query.Param("start") Instant start,
            @org.springframework.data.repository.query.Param("end") Instant end);
}

package com.example.j2ee16.repository;

import com.example.j2ee16.entity.Ticket;
import com.example.j2ee16.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByTripIdAndTicketStatus(Long tripId, TicketStatus status);

    long countByTripIdAndTicketStatus(Long tripId, TicketStatus status);

    List<Ticket> findByTripId(Long tripId);

    List<Ticket> findByBookingId(Long bookingId);

    List<Ticket> findByBookingIdIn(List<Long> bookingIds);

    java.util.Optional<Ticket> findByTicketCode(String ticketCode);

    @org.springframework.data.jpa.repository.Query("SELECT t FROM Ticket t WHERE t.ticketStatus = :status AND t.reminderSent = false AND t.trip.departureTime BETWEEN :start AND :end")
    List<Ticket> findUpcomingTicketsForReminder(
        @org.springframework.data.repository.query.Param("status") TicketStatus status, 
        @org.springframework.data.repository.query.Param("start") java.time.Instant start, 
        @org.springframework.data.repository.query.Param("end") java.time.Instant end
    );
}

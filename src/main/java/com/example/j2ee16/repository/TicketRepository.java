package com.example.j2ee16.repository;

import com.example.j2ee16.entity.Ticket;
import com.example.j2ee16.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByTripIdAndTicketStatus(Long tripId, TicketStatus status);
}

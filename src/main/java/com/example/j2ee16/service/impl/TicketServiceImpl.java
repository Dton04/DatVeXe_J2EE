package com.example.j2ee16.service.impl;

import com.example.j2ee16.constants.ErrorCodeConstants;
import com.example.j2ee16.dto.response.TicketResponse;
import com.example.j2ee16.entity.CheckInStatus;
import com.example.j2ee16.entity.Ticket;
import com.example.j2ee16.exception.ApiException;
import com.example.j2ee16.repository.TicketRepository;
import com.example.j2ee16.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    @Transactional
    public TicketResponse checkIn(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Ticket not found"));

        ticket.setCheckInStatus(CheckInStatus.ON_BOARD);
        Ticket savedTicket = ticketRepository.save(ticket);

        return mapToResponse(savedTicket);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> getPassengersByTrip(Long tripId) {
        List<Ticket> tickets = ticketRepository.findByTripId(tripId);
        return tickets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getSeatNumber(),
                ticket.getPassengerName(),
                ticket.getPhone(),
                ticket.getPrice(),
                ticket.getTicketStatus(),
                ticket.getCheckInStatus()
        );
    }
}

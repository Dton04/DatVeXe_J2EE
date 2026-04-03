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
    @Transactional
    public TicketResponse checkInByCode(String ticketCode) {
        // Try numeric ID first (backward compat), then ticket_code
        Ticket ticket;
        try {
            Long numId = Long.parseLong(ticketCode.trim());
            ticket = ticketRepository.findById(numId)
                    .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Ticket not found"));
        } catch (NumberFormatException e) {
            ticket = ticketRepository.findByTicketCode(ticketCode.trim().toUpperCase())
                    .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Ticket not found: " + ticketCode));
        }

        if (ticket.getCheckInStatus() == CheckInStatus.ON_BOARD) {
            throw new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.CONFLICT, "Ticket already checked in");
        }

        ticket.setCheckInStatus(CheckInStatus.ON_BOARD);
        return mapToResponse(ticketRepository.save(ticket));
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
                ticket.getTicketCode(),
                ticket.getSeatNumber(),
                ticket.getPassengerName(),
                ticket.getPhone(),
                ticket.getPrice(),
                ticket.getTicketStatus(),
                ticket.getCheckInStatus()
        );
    }
}

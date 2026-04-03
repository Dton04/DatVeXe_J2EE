package com.example.j2ee16.service;

import com.example.j2ee16.dto.response.TicketResponse;
import java.util.List;

public interface TicketService {
    TicketResponse checkIn(Long ticketId);
    /** Check-in by human-readable ticket code (e.g. TK6-260402-A1) */
    TicketResponse checkInByCode(String ticketCode);
    List<TicketResponse> getPassengersByTrip(Long tripId);
}

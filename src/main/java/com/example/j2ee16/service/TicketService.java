package com.example.j2ee16.service;

import com.example.j2ee16.dto.response.TicketResponse;
import java.util.List;

public interface TicketService {
    TicketResponse checkIn(Long ticketId);
    List<TicketResponse> getPassengersByTrip(Long tripId);
}

package com.example.j2ee16.controller;

import com.example.j2ee16.dto.response.TicketResponse;
import com.example.j2ee16.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * Check-in by ticket_code (e.g. TK6-260402-A1) or by numeric ID.
     * Both formats are supported for backward compatibility.
     * Example: PATCH /api/v1/tickets/TK6-260402-A1/check-in
     */
    @PatchMapping("/{ticketCode}/check-in")
    public ResponseEntity<TicketResponse> checkIn(@PathVariable String ticketCode) {
        return ResponseEntity.ok(ticketService.checkInByCode(ticketCode));
    }
}

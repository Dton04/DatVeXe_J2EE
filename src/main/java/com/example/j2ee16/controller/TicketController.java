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

    @PatchMapping("/{id}/check-in")
    public ResponseEntity<TicketResponse> checkIn(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.checkIn(id));
    }
}

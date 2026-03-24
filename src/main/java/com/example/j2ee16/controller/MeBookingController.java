package com.example.j2ee16.controller;

import com.example.j2ee16.dto.response.MyBookingResponse;
import com.example.j2ee16.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/me")
public class MeBookingController {
    private final BookingService bookingService;

    public MeBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<MyBookingResponse>> getMyBookings(
            Authentication authentication,
            @RequestParam(value = "type", defaultValue = "UPCOMING") String type
    ) {
        return ResponseEntity.ok(bookingService.getMyBookings(authentication.getName(), type));
    }
}


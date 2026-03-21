package com.example.j2ee16.controller;

import com.example.j2ee16.dto.response.TripSearchResponse;
import com.example.j2ee16.service.TripService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/trips")
public class PublicTripController {
    private final TripService tripService;

    public PublicTripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping
    public ResponseEntity<List<TripSearchResponse>> searchTrips(
            @RequestParam("origin_id") Long originId,
            @RequestParam("destination_id") Long destinationId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "max_legs", defaultValue = "2") Integer maxLegs,
            @RequestParam(value = "min_layover_minutes", defaultValue = "30") Integer minLayoverMinutes) {

        List<TripSearchResponse> results = tripService.searchTrips(originId, destinationId, date, maxLegs, minLayoverMinutes);
        return ResponseEntity.ok(results);
    }
}

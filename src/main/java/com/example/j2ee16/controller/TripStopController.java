package com.example.j2ee16.controller;

import com.example.j2ee16.dto.request.TripStopRequest;
import com.example.j2ee16.dto.response.TripStopResponse;
import com.example.j2ee16.service.TripStopService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1")
public class TripStopController {
    private final TripStopService tripStopService;

    public TripStopController(TripStopService tripStopService) {
        this.tripStopService = tripStopService;
    }

    // Public endpoint
    @GetMapping("/trips/{id}/stops")
    public ResponseEntity<List<TripStopResponse>> getStopsByTrip(@PathVariable Long id) {
        return ResponseEntity.ok(tripStopService.getStopsByTrip(id));
    }

    // Admin endpoints
    @PostMapping("/admin/trips/{id}/stops")
    public ResponseEntity<TripStopResponse> addStop(
            @PathVariable Long id,
            @Valid @RequestBody TripStopRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tripStopService.addStop(id, request));
    }

    @PutMapping("/admin/trips/{id}/stops/{stop_id}")
    public ResponseEntity<TripStopResponse> updateStop(
            @PathVariable Long id,
            @PathVariable Long stop_id,
            @Valid @RequestBody TripStopRequest request
    ) {
        return ResponseEntity.ok(tripStopService.updateStop(id, stop_id, request));
    }

    @DeleteMapping("/admin/trips/{id}/stops/{stop_id}")
    public ResponseEntity<Void> deleteStop(
            @PathVariable Long id,
            @PathVariable Long stop_id
    ) {
        tripStopService.deleteStop(id, stop_id);
        return ResponseEntity.noContent().build();
    }
}

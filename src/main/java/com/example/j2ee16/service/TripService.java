package com.example.j2ee16.service;

import com.example.j2ee16.dto.request.TripRequest;
import com.example.j2ee16.dto.response.TripResponse;

import com.example.j2ee16.dto.response.TripSearchResponse;

import java.time.LocalDate;
import java.util.List;

import java.util.Map;

public interface TripService {
    TripResponse createTrip(TripRequest request);

    List<TripSearchResponse> searchTrips(Long originId, Long destinationId, LocalDate date, Integer maxLegs,
            Integer minLayoverMinutes);

    Map<String, String> getSeatMap(Long tripId);
}

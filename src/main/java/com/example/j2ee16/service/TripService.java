package com.example.j2ee16.service;

import com.example.j2ee16.dto.request.TripRequest;
import com.example.j2ee16.dto.response.TripResponse;

import com.example.j2ee16.dto.response.TripSearchResponse;

import java.time.LocalDate;
import java.util.List;

import java.util.Map;

public interface TripService {
    TripResponse createTrip(TripRequest request);

    TripResponse updateTrip(Long id, TripRequest request);

    TripResponse updateTripStatus(Long id, String status);

    List<TripResponse> getAllTrips();

    List<TripSearchResponse> searchTrips(Long originProvinceId, Long destinationProvinceId, LocalDate date, Integer maxLegs,
            Integer minLayoverMinutes);

    Map<String, String> getSeatMap(Long tripId);
}

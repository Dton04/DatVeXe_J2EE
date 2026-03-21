package com.example.j2ee16.service;

import com.example.j2ee16.dto.request.TripStopRequest;
import com.example.j2ee16.dto.response.TripStopResponse;

import java.util.List;

public interface TripStopService {
    List<TripStopResponse> getStopsByTrip(Long tripId);
    TripStopResponse addStop(Long tripId, TripStopRequest request);
    TripStopResponse updateStop(Long tripId, Long stopId, TripStopRequest request);
    void deleteStop(Long tripId, Long stopId);
}

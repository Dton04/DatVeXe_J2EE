package com.example.j2ee16.service;

import com.example.j2ee16.dto.request.TripRequest;
import com.example.j2ee16.dto.response.TripResponse;

public interface TripService {
    TripResponse createTrip(TripRequest request);
}

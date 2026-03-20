package com.example.j2ee16.service;

import com.example.j2ee16.dto.request.StationRequest;
import com.example.j2ee16.dto.response.StationResponse;

import java.util.List;

public interface StationService {
    List<StationResponse> getAllStations();
    StationResponse createStation(StationRequest request);
    StationResponse updateStation(Long id, StationRequest request);
    void deleteStation(Long id);
}

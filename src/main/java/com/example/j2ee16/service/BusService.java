package com.example.j2ee16.service;

import com.example.j2ee16.dto.request.BusRequest;
import com.example.j2ee16.dto.response.BusResponse;

import java.util.List;

public interface BusService {
    BusResponse createBus(BusRequest request);
    void generateSeatsForBus(Long busId);
    List<BusResponse> getAllBuses();
}

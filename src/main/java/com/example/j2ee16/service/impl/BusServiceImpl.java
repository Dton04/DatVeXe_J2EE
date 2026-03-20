package com.example.j2ee16.service.impl;

import com.example.j2ee16.constants.ErrorCodeConstants;
import com.example.j2ee16.dto.request.BusRequest;
import com.example.j2ee16.dto.response.BusResponse;
import com.example.j2ee16.entity.Bus;
import com.example.j2ee16.entity.BusStatus;
import com.example.j2ee16.exception.ApiException;
import com.example.j2ee16.repository.BusRepository;
import com.example.j2ee16.service.BusService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BusServiceImpl implements BusService {
    private final BusRepository busRepository;

    public BusServiceImpl(BusRepository busRepository) {
        this.busRepository = busRepository;
    }

    @Override
    @Transactional
    public BusResponse createBus(BusRequest request) {
        if (busRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new ApiException(
                    ErrorCodeConstants.INTERNAL_SERVER_ERROR, // Should add LICENSE_PLATE_ALREADY_EXISTS
                    HttpStatus.CONFLICT,
                    "Bus with this license plate already exists");
        }

        Bus bus = new Bus();
        bus.setLicensePlate(request.getLicensePlate());
        bus.setTotalSeats(request.getTotalSeats());
        bus.setBusType(request.getBusType());
        bus.setStatus(BusStatus.ACTIVE);

        Bus savedBus = busRepository.save(bus);
        return new BusResponse(
                savedBus.getId(),
                savedBus.getLicensePlate(),
                savedBus.getTotalSeats(),
                savedBus.getBusType());
    }
}

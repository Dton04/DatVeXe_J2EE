package com.example.j2ee16.service.impl;

import com.example.j2ee16.constants.ErrorCodeConstants;
import com.example.j2ee16.dto.request.BusRequest;
import com.example.j2ee16.dto.response.BusResponse;
import com.example.j2ee16.entity.Bus;
import com.example.j2ee16.entity.BusStatus;
import com.example.j2ee16.exception.ApiException;
import com.example.j2ee16.entity.Seat;
import com.example.j2ee16.repository.BusRepository;
import com.example.j2ee16.repository.SeatRepository;
import com.example.j2ee16.service.BusService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BusServiceImpl implements BusService {
    private final BusRepository busRepository;
    private final SeatRepository seatRepository;

    public BusServiceImpl(BusRepository busRepository, SeatRepository seatRepository) {
        this.busRepository = busRepository;
        this.seatRepository = seatRepository;
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

        // Auto-generate seats
        generateSeats(savedBus);

        return new BusResponse(
                savedBus.getId(),
                savedBus.getLicensePlate(),
                savedBus.getTotalSeats(),
                savedBus.getBusType());
    }

    @Override
    @Transactional
    public void generateSeatsForBus(Long busId) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Bus not found"));
        
        // Remove existing seats if any (to avoid duplicates if re-running)
        List<Seat> existingSeats = seatRepository.findByBusId(busId);
        if (!existingSeats.isEmpty()) {
            seatRepository.deleteAll(existingSeats);
        }
        
        generateSeats(bus);
    }

    private void generateSeats(Bus bus) {
        int totalSeats = bus.getTotalSeats();
        int rows = (int) Math.ceil((double) totalSeats / 2);
        
        for (int i = 0; i < totalSeats; i++) {
            Seat seat = new Seat();
            seat.setBus(bus);
            
            // Generate label: A1, A2, ..., B1, B2...
            String prefix = (i < rows) ? "A" : "B";
            int number = (i % rows) + 1;
            seat.setSeatNumber(prefix + number);
            
            seatRepository.save(seat);
        }
    }
}

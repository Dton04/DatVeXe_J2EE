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

import java.util.ArrayList;
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
                    ErrorCodeConstants.INTERNAL_SERVER_ERROR,
                    HttpStatus.CONFLICT,
                    "Xe với biển số này đã tồn tại");
        }

        Bus bus = new Bus();
        bus.setLicensePlate(request.getLicensePlate());
        bus.setTotalSeats(request.getTotalSeats());
        bus.setBusType(request.getBusType());
        bus.setStatus(BusStatus.ACTIVE);

        Bus savedBus = busRepository.save(bus);

        // Auto-generate seats right after creating bus
        int seatsCreated = generateSeats(savedBus);

        return new BusResponse(
                savedBus.getId(),
                savedBus.getLicensePlate(),
                savedBus.getTotalSeats(),
                savedBus.getBusType(),
                seatsCreated);
    }

    @Override
    @Transactional
    public void generateSeatsForBus(Long busId) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Bus not found"));

        // Remove existing seats to avoid duplicates
        List<Seat> existingSeats = seatRepository.findByBusId(busId);
        if (!existingSeats.isEmpty()) {
            seatRepository.deleteAll(existingSeats);
            seatRepository.flush();
        }

        generateSeats(bus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> getAllBuses() {
        return busRepository.findAll().stream()
                .map(bus -> new BusResponse(
                        bus.getId(),
                        bus.getLicensePlate(),
                        bus.getTotalSeats(),
                        bus.getBusType(),
                        (int) seatRepository.findByBusId(bus.getId()).size()
                ))
                .toList();
    }

    /**
     * Generate seats for a bus and return the number of seats created.
     * Seat label scheme:
     *   - For standard buses (2 columns): A1..An | B1..Bn
     *   - For large buses (3+ columns possible): uses rows of 4 (A,B left | C,D right)
     * Currently using simple A/B column scheme.
     */
    private int generateSeats(Bus bus) {
        int totalSeats = bus.getTotalSeats();
        int rows = (int) Math.ceil((double) totalSeats / 2);

        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < totalSeats; i++) {
            Seat seat = new Seat();
            seat.setBus(bus);

            // A column first, then B column
            String col = (i < rows) ? "A" : "B";
            int rowNum = (i % rows) + 1;
            seat.setSeatNumber(col + rowNum);

            seats.add(seat);
        }

        seatRepository.saveAll(seats);
        return seats.size();
    }
}

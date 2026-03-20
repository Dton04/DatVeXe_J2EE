package com.example.j2ee16.service.impl;

import com.example.j2ee16.constants.ErrorCodeConstants;
import com.example.j2ee16.dto.request.StationRequest;
import com.example.j2ee16.dto.response.StationResponse;
import com.example.j2ee16.entity.Station;
import com.example.j2ee16.exception.ApiException;
import com.example.j2ee16.repository.StationRepository;
import com.example.j2ee16.service.StationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationServiceImpl implements StationService {
    private final StationRepository stationRepository;

    public StationServiceImpl(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Override
    public List<StationResponse> getAllStations() {
        return stationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StationResponse createStation(StationRequest request) {
        Station station = new Station();
        station.setName(request.getName());
        station.setCity(request.getCity());
        station.setAddress(request.getAddress());

        return mapToResponse(stationRepository.save(station));
    }

    @Override
    @Transactional
    public StationResponse updateStation(Long id, StationRequest request) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        ErrorCodeConstants.INTERNAL_SERVER_ERROR, // Should probably add STATION_NOT_FOUND
                        HttpStatus.NOT_FOUND,
                        "Station not found"
                ));

        station.setName(request.getName());
        station.setCity(request.getCity());
        station.setAddress(request.getAddress());

        return mapToResponse(stationRepository.save(station));
    }

    @Override
    @Transactional
    public void deleteStation(Long id) {
        if (!stationRepository.existsById(id)) {
            throw new ApiException(
                    ErrorCodeConstants.INTERNAL_SERVER_ERROR,
                    HttpStatus.NOT_FOUND,
                    "Station not found"
            );
        }
        stationRepository.deleteById(id);
    }

    private StationResponse mapToResponse(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName(),
                station.getCity(),
                station.getAddress()
        );
    }
}

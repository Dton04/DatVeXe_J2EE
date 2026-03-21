package com.example.j2ee16.service.impl;

import com.example.j2ee16.constants.ErrorCodeConstants;
import com.example.j2ee16.dto.request.TripStopRequest;
import com.example.j2ee16.dto.response.TripStopResponse;
import com.example.j2ee16.entity.Station;
import com.example.j2ee16.entity.Trip;
import com.example.j2ee16.entity.TripStop;
import com.example.j2ee16.exception.ApiException;
import com.example.j2ee16.repository.StationRepository;
import com.example.j2ee16.repository.TripRepository;
import com.example.j2ee16.repository.TripStopRepository;
import com.example.j2ee16.service.TripStopService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripStopServiceImpl implements TripStopService {
    private final TripStopRepository tripStopRepository;
    private final TripRepository tripRepository;
    private final StationRepository stationRepository;

    public TripStopServiceImpl(TripStopRepository tripStopRepository, TripRepository tripRepository, StationRepository stationRepository) {
        this.tripStopRepository = tripStopRepository;
        this.tripRepository = tripRepository;
        this.stationRepository = stationRepository;
    }

    @Override
    public List<TripStopResponse> getStopsByTrip(Long tripId) {
        if (!tripRepository.existsById(tripId)) {
            throw new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.NOT_FOUND, "Trip not found");
        }
        return tripStopRepository.findByTripIdOrderByOrderIndexAsc(tripId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TripStopResponse addStop(Long tripId, TripStopRequest request) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.NOT_FOUND, "Trip not found"));

        Station station = stationRepository.findById(request.getStationId())
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.NOT_FOUND, "Station not found"));

        TripStop stop = new TripStop();
        stop.setTrip(trip);
        stop.setStation(station);
        stop.setStopType(request.getStopType());
        stop.setStopTime(request.getStopTime());
        stop.setAddressDetail(request.getAddressDetail());
        stop.setOrderIndex(request.getOrderIndex());

        return mapToResponse(tripStopRepository.save(stop));
    }

    @Override
    @Transactional
    public TripStopResponse updateStop(Long tripId, Long stopId, TripStopRequest request) {
        TripStop stop = tripStopRepository.findById(stopId)
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.NOT_FOUND, "Trip stop not found"));

        if (!stop.getTrip().getId().equals(tripId)) {
            throw new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.BAD_REQUEST, "Stop does not belong to this trip");
        }

        Station station = stationRepository.findById(request.getStationId())
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.NOT_FOUND, "Station not found"));

        stop.setStation(station);
        stop.setStopType(request.getStopType());
        stop.setStopTime(request.getStopTime());
        stop.setAddressDetail(request.getAddressDetail());
        stop.setOrderIndex(request.getOrderIndex());

        return mapToResponse(tripStopRepository.save(stop));
    }

    @Override
    @Transactional
    public void deleteStop(Long tripId, Long stopId) {
        TripStop stop = tripStopRepository.findById(stopId)
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.NOT_FOUND, "Trip stop not found"));

        if (!stop.getTrip().getId().equals(tripId)) {
            throw new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.BAD_REQUEST, "Stop does not belong to this trip");
        }

        tripStopRepository.delete(stop);
    }

    private TripStopResponse mapToResponse(TripStop stop) {
        return new TripStopResponse(
                stop.getId(),
                stop.getStation().getName(),
                stop.getStation().getCity(),
                stop.getStopType(),
                stop.getStopTime(),
                stop.getAddressDetail(),
                stop.getOrderIndex()
        );
    }
}

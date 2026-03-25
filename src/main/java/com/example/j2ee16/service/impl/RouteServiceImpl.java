package com.example.j2ee16.service.impl;

import com.example.j2ee16.constants.ErrorCodeConstants;
import com.example.j2ee16.dto.request.RouteRequest;
import com.example.j2ee16.dto.response.RouteResponse;
import com.example.j2ee16.dto.response.StationResponse;
import com.example.j2ee16.entity.Route;
import com.example.j2ee16.entity.Station;
import com.example.j2ee16.exception.ApiException;
import com.example.j2ee16.repository.RouteRepository;
import com.example.j2ee16.repository.StationRepository;
import com.example.j2ee16.service.RouteService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {
    private final RouteRepository routeRepository;
    private final StationRepository stationRepository;

    public RouteServiceImpl(RouteRepository routeRepository, StationRepository stationRepository) {
        this.routeRepository = routeRepository;
        this.stationRepository = stationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RouteResponse createRoute(RouteRequest request) {
        if (request.getOriginStationId().equals(request.getDestinationStationId())) {
            throw new ApiException(
                    ErrorCodeConstants.VALIDATION_ERROR,
                    HttpStatus.BAD_REQUEST,
                    "Origin and destination cannot be the same"
            );
        }

        Station origin = stationRepository.findById(request.getOriginStationId())
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Origin station not found"));

        Station destination = stationRepository.findById(request.getDestinationStationId())
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Destination station not found"));

        Route route = new Route();
        route.setOrigin(origin);
        route.setDestination(destination);
        route.setBasePrice(request.getBasePrice());
        route.setDistanceKm(request.getDistanceKm());
        route.setEstimatedDuration(request.getEstimatedDuration());

        return mapToResponse(routeRepository.save(route));
    }

    private RouteResponse mapToResponse(Route route) {
        return new RouteResponse(
                route.getId(),
                route.getOrigin().getName() + " - " + route.getDestination().getName(),
                route.getBasePrice(),
                route.getDistanceKm(),
                route.getEstimatedDuration(),
                new StationResponse(route.getOrigin().getId(), route.getOrigin().getName(), route.getOrigin().getCity(), route.getOrigin().getAddress()),
                new StationResponse(route.getDestination().getId(), route.getDestination().getName(), route.getDestination().getCity(), route.getDestination().getAddress())
        );
    }

    @Override
    @Transactional
    public RouteResponse updateRoute(Long id, RouteRequest request) {
        if (request.getOriginStationId().equals(request.getDestinationStationId())) {
            throw new ApiException(
                    ErrorCodeConstants.VALIDATION_ERROR,
                    HttpStatus.BAD_REQUEST,
                    "Origin and destination cannot be the same"
            );
        }

        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Route not found"));

        Station origin = stationRepository.findById(request.getOriginStationId())
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Origin station not found"));

        Station destination = stationRepository.findById(request.getDestinationStationId())
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Destination station not found"));

        route.setOrigin(origin);
        route.setDestination(destination);
        route.setBasePrice(request.getBasePrice());
        route.setDistanceKm(request.getDistanceKm());
        route.setEstimatedDuration(request.getEstimatedDuration());

        return mapToResponse(routeRepository.save(route));
    }

    @Override
    @Transactional
    public void deleteRoute(Long id) {
        if (!routeRepository.existsById(id)) {
            throw new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Route not found");
        }
        try {
            routeRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new ApiException(
                    ErrorCodeConstants.VALIDATION_ERROR,
                    HttpStatus.CONFLICT,
                    "Route cannot be deleted because it is being used"
            );
        }
    }
}

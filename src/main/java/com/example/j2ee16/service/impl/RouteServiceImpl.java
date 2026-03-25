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
import com.example.j2ee16.repository.TripRepository;
import com.example.j2ee16.service.RouteService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {
    private final RouteRepository routeRepository;
    private final StationRepository stationRepository;
    private final TripRepository tripRepository;

    public RouteServiceImpl(RouteRepository routeRepository, StationRepository stationRepository, TripRepository tripRepository) {
        this.routeRepository = routeRepository;
        this.stationRepository = stationRepository;
        this.tripRepository = tripRepository;
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
        Station origin = stationRepository.findById(request.getOriginStationId())
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.NOT_FOUND, "Origin station not found"));

        Station destination = stationRepository.findById(request.getDestinationStationId())
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.NOT_FOUND, "Destination station not found"));

        Route route = new Route();
        route.setOrigin(origin);
        route.setDestination(destination);
        route.setBasePrice(request.getBasePrice());
        route.setDistanceKm(request.getDistanceKm());
        route.setEstimatedDuration(request.getEstimatedDuration());
        route.setDepartureDate(request.getDepartureDate());

        return mapToResponse(routeRepository.save(route));
    }

    @Override
    @Transactional
    public void deleteRoute(Long id) {
        if (!routeRepository.existsById(id)) {
            throw new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.NOT_FOUND, "Tuyến đường không tồn tại");
        }
        
        if (tripRepository.existsByRouteId(id)) {
            throw new ApiException(ErrorCodeConstants.VALIDATION_ERROR, HttpStatus.CONFLICT, "Không thể xóa tuyến đường này vì đã có chuyến xe đang hoạt động.");
        }
        
        try {
            routeRepository.deleteById(id);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new ApiException(ErrorCodeConstants.VALIDATION_ERROR, HttpStatus.CONFLICT, "Không thể xóa tuyến đường này do ràng buộc dữ liệu khác.");
        }
    }

    private RouteResponse mapToResponse(Route route) {
        RouteResponse response = new RouteResponse();
        response.setId(route.getId());
        response.setName(route.getOrigin().getName() + " - " + route.getDestination().getName());
        response.setPrice(route.getBasePrice());
        response.setDeparture(route.getOrigin().getCity());
        response.setDestination(route.getDestination().getCity());
        response.setDistance(route.getDistanceKm());
        response.setDuration(route.getEstimatedDuration());
        response.setDepartureDate(route.getDepartureDate());
        
        Station origin = route.getOrigin();
        response.setOriginStation(new StationResponse(origin.getId(), origin.getName(), origin.getCity(), origin.getAddress()));
        
        Station destination = route.getDestination();
        response.setDestinationStation(new StationResponse(destination.getId(), destination.getName(), destination.getCity(), destination.getAddress()));
        
        return response;
    }
}

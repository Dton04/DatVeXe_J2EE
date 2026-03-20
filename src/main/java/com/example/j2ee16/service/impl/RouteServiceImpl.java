package com.example.j2ee16.service.impl;

import com.example.j2ee16.dto.response.RouteResponse;
import com.example.j2ee16.repository.RouteRepository;
import com.example.j2ee16.service.RouteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {
    private final RouteRepository routeRepository;

    public RouteServiceImpl(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(route -> new RouteResponse(
                        route.getId(),
                        route.getOrigin().getName() + " - " + route.getDestination().getName(),
                        route.getBasePrice()
                ))
                .collect(Collectors.toList());
    }
}

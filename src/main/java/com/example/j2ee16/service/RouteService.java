package com.example.j2ee16.service;

import com.example.j2ee16.dto.request.RouteRequest;
import com.example.j2ee16.dto.response.RouteResponse;

import java.util.List;

public interface RouteService {
    List<RouteResponse> getAllRoutes();
    RouteResponse createRoute(RouteRequest request);
}

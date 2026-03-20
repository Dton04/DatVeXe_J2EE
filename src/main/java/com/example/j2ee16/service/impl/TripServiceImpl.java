package com.example.j2ee16.service.impl;

import com.example.j2ee16.constants.ErrorCodeConstants;
import com.example.j2ee16.dto.request.TripRequest;
import com.example.j2ee16.dto.response.TripResponse;
import com.example.j2ee16.entity.Bus;
import com.example.j2ee16.entity.Route;
import com.example.j2ee16.entity.Trip;
import com.example.j2ee16.entity.TripStatus;
import com.example.j2ee16.exception.ApiException;
import com.example.j2ee16.repository.BusRepository;
import com.example.j2ee16.repository.RouteRepository;
import com.example.j2ee16.repository.TripRepository;
import com.example.j2ee16.service.TripService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TripServiceImpl implements TripService {
        private final TripRepository tripRepository;
        private final RouteRepository routeRepository;
        private final BusRepository busRepository;

        public TripServiceImpl(TripRepository tripRepository, RouteRepository routeRepository,
                        BusRepository busRepository) {
                this.tripRepository = tripRepository;
                this.routeRepository = routeRepository;
                this.busRepository = busRepository;
        }

        @Override
        @Transactional
        public TripResponse createTrip(TripRequest request) {
                Route route = routeRepository.findById(request.getRouteId())
                                .orElseThrow(() -> new ApiException(
                                                ErrorCodeConstants.INTERNAL_SERVER_ERROR,
                                                HttpStatus.NOT_FOUND,
                                                "Route not found"));

                Bus bus = busRepository.findById(request.getBusId())
                                .orElseThrow(() -> new ApiException(
                                                ErrorCodeConstants.INTERNAL_SERVER_ERROR,
                                                HttpStatus.NOT_FOUND,
                                                "Bus not found"));

                Trip trip = new Trip();
                trip.setRoute(route);
                trip.setBus(bus);
                trip.setDepartureTime(request.getDepartureTime());
                trip.setActualPrice(request.getPriceModifier());
                trip.setStatus(TripStatus.SCHEDULED);

                // Calculate arrival time if estimated duration is available
                if (route.getEstimatedDuration() != null) {
                        trip.setArrivalTime(request.getDepartureTime().plusMinutes(route.getEstimatedDuration()));
                }

                Trip savedTrip = tripRepository.save(trip);

                return new TripResponse(
                                savedTrip.getId(),
                                route.getOrigin().getName() + " - " + route.getDestination().getName(),
                                bus.getLicensePlate(),
                                savedTrip.getDepartureTime(),
                                savedTrip.getActualPrice());
        }
}

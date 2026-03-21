package com.example.j2ee16.service.impl;

import com.example.j2ee16.constants.ErrorCodeConstants;
import com.example.j2ee16.dto.request.TripRequest;
import com.example.j2ee16.dto.response.TripLegResponse;
import com.example.j2ee16.dto.response.TripResponse;
import com.example.j2ee16.dto.response.TripSearchResponse;
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

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                                            ErrorCodeConstants.RESOURCE_NOT_FOUND,
                                            HttpStatus.NOT_FOUND,
                                            "Route not found"));

            Bus bus = busRepository.findById(request.getBusId())
                            .orElseThrow(() -> new ApiException(
                                            ErrorCodeConstants.RESOURCE_NOT_FOUND,
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
                    trip.setArrivalTime(request.getDepartureTime().plus(Duration.ofMinutes(route.getEstimatedDuration())));
            }

            Trip savedTrip = tripRepository.save(trip);

            return new TripResponse(
                            savedTrip.getId(),
                            route.getOrigin().getName() + " - " + route.getDestination().getName(),
                            bus.getLicensePlate(),
                            savedTrip.getDepartureTime(),
                            savedTrip.getActualPrice());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripSearchResponse> searchTrips(Long originId, Long destinationId, LocalDate date, Integer maxLegs, Integer minLayoverMinutes) {
        Instant startOfDay = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        List<TripSearchResponse> results = new ArrayList<>();

        // 1. Direct Trips
        List<Trip> directTrips = tripRepository.findByRouteOriginIdAndRouteDestinationIdAndDepartureTimeBetween(
                originId, destinationId, startOfDay, endOfDay);
        
        for (Trip trip : directTrips) {
            TripLegResponse leg = createLegResponse(trip);
            results.add(new TripSearchResponse("DIRECT", trip.getActualPrice(), null, Arrays.asList(leg)));
        }

        // 2. Connecting Trips (if maxLegs >= 2)
        if (maxLegs != null && maxLegs >= 2) {
            List<Trip> firstLegs = tripRepository.findByRouteOriginIdAndRouteDestinationIdNotAndDepartureTimeBetween(
                    originId, destinationId, startOfDay, endOfDay);

            int layover = minLayoverMinutes != null ? minLayoverMinutes : 30;

            for (Trip firstLeg : firstLegs) {
                if (firstLeg.getArrivalTime() == null) continue;

                Instant minEarliestDeparture = firstLeg.getArrivalTime().plus(Duration.ofMinutes(layover));
                
                List<Trip> secondLegs = tripRepository.findByRouteOriginIdAndRouteDestinationIdAndDepartureTimeGreaterThanEqual(
                        firstLeg.getRoute().getDestination().getId(), destinationId, minEarliestDeparture);

                for (Trip secondLeg : secondLegs) {
                    // Limit connecting trips to within 24 hours of first leg arrival to remain relevant
                    if(secondLeg.getDepartureTime().isAfter(firstLeg.getArrivalTime().plus(Duration.ofHours(24)))) {
                        continue;
                    }

                    TripLegResponse leg1 = createLegResponse(firstLeg);
                    TripLegResponse leg2 = createLegResponse(secondLeg);

                    BigDecimal totalPrice = firstLeg.getActualPrice().add(secondLeg.getActualPrice());
                    long layoverDuration = Duration.between(firstLeg.getArrivalTime(), secondLeg.getDepartureTime()).toMinutes();
                    String layoverStr = layoverDuration + " mins";

                    results.add(new TripSearchResponse("CONNECTING", totalPrice, layoverStr, Arrays.asList(leg1, leg2)));
                }
            }
        }

        return results;
    }

    private TripLegResponse createLegResponse(Trip trip) {
        return new TripLegResponse(
                trip.getId(),
                trip.getRoute().getOrigin().getName(),
                trip.getRoute().getDestination().getName(),
                trip.getDepartureTime(),
                trip.getArrivalTime(),
                trip.getBus().getBusType() != null ? trip.getBus().getBusType() : "Standard"
        );
    }
}

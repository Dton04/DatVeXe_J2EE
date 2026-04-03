package com.example.j2ee16.service.impl;

import com.example.j2ee16.constants.ErrorCodeConstants;
import com.example.j2ee16.dto.request.TripRequest;
import com.example.j2ee16.dto.response.TripLegResponse;
import com.example.j2ee16.dto.response.TripResponse;
import com.example.j2ee16.dto.response.TripSearchResponse;
import com.example.j2ee16.entity.*;
import com.example.j2ee16.exception.ApiException;
import com.example.j2ee16.repository.*;
import com.example.j2ee16.service.TripService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;

@Service
public class TripServiceImpl implements TripService {
    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;
    private final BusRepository busRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final SeatHoldRepository seatHoldRepository;
    private final UserRepository userRepository;

    public TripServiceImpl(TripRepository tripRepository, RouteRepository routeRepository,
                           BusRepository busRepository, SeatRepository seatRepository,
                           TicketRepository ticketRepository, SeatHoldRepository seatHoldRepository,
                           UserRepository userRepository) {
        this.tripRepository = tripRepository;
        this.routeRepository = routeRepository;
        this.busRepository = busRepository;
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
        this.seatHoldRepository = seatHoldRepository;
        this.userRepository = userRepository;
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

            // FIX: actual_price = base_price * price_modifier (not the modifier itself)
            BigDecimal basePrice = route.getBasePrice() != null ? route.getBasePrice() : BigDecimal.ZERO;
            BigDecimal modifier = request.getPriceModifier() != null ? request.getPriceModifier() : BigDecimal.ONE;
            trip.setActualPrice(basePrice.multiply(modifier));

            trip.setStatus(TripStatus.SCHEDULED);

            if (request.getDriverId() != null) {
                User driver = userRepository.findById(request.getDriverId())
                        .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Driver not found"));
                if (driver.getRole() != UserRole.DRIVER) {
                    throw new ApiException(ErrorCodeConstants.VALIDATION_ERROR, HttpStatus.BAD_REQUEST, "User is not a driver");
                }
                trip.setDriver(driver);
            }

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
    public List<TripResponse> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(trip -> new TripResponse(
                        trip.getId(),
                        trip.getRoute().getOrigin().getName() + " - " + trip.getRoute().getDestination().getName(),
                        trip.getBus().getLicensePlate(),
                        trip.getBus().getBusType(),
                        trip.getBus().getTotalSeats(),
                        trip.getDepartureTime(),
                        trip.getArrivalTime(),
                        trip.getActualPrice(),
                        getComputedStatus(trip),
                        trip.getDriver() != null ? trip.getDriver().getId() : null,
                        trip.getDriver() != null ? trip.getDriver().getFullName() : null
                ))
                .toList();
    }

    private String getComputedStatus(Trip trip) {
        if (trip.getStatus() == null) return "SCHEDULED";
        if (trip.getStatus() == TripStatus.CANCELLED || trip.getStatus() == TripStatus.DELAYED) {
            return trip.getStatus().name();
        }
        Instant now = Instant.now();
        if (trip.getArrivalTime() != null && now.isAfter(trip.getArrivalTime())) {
            return TripStatus.COMPLETED.name();
        } else if (trip.getDepartureTime() != null && now.isAfter(trip.getDepartureTime())) {
            if (trip.getArrivalTime() == null && now.isAfter(trip.getDepartureTime().plus(24, ChronoUnit.HOURS))) {
                return TripStatus.COMPLETED.name();
            }
            return TripStatus.IN_PROGRESS.name();
        }
        return trip.getStatus().name();
    }

    @Override
    @Transactional
    public TripResponse updateTripStatus(Long id, String status) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Trip not found"));
        trip.setStatus(TripStatus.valueOf(status));
        Trip saved = tripRepository.save(trip);
        return new TripResponse(
                saved.getId(),
                saved.getRoute().getOrigin().getName() + " - " + saved.getRoute().getDestination().getName(),
                saved.getBus().getLicensePlate(),
                saved.getBus().getBusType(),
                saved.getBus().getTotalSeats(),
                saved.getDepartureTime(),
                saved.getArrivalTime(),
                saved.getActualPrice(),
                saved.getStatus() != null ? saved.getStatus().name() : null);
    }

    @Override
    @Transactional
    public TripResponse updateTrip(Long id, TripRequest request) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Trip not found"));

        if (request.getRouteId() != null) {
            Route route = routeRepository.findById(request.getRouteId())
                    .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Route not found"));
            trip.setRoute(route);

            // Recalculate price with new route's base price
            BigDecimal basePrice = route.getBasePrice() != null ? route.getBasePrice() : BigDecimal.ZERO;
            BigDecimal modifier = request.getPriceModifier() != null ? request.getPriceModifier() : BigDecimal.ONE;
            trip.setActualPrice(basePrice.multiply(modifier));

            if (route.getEstimatedDuration() != null && trip.getDepartureTime() != null) {
                trip.setArrivalTime(trip.getDepartureTime().plus(Duration.ofMinutes(route.getEstimatedDuration())));
            }
        } else if (request.getPriceModifier() != null && trip.getRoute() != null) {
            BigDecimal basePrice = trip.getRoute().getBasePrice() != null ? trip.getRoute().getBasePrice() : BigDecimal.ZERO;
            trip.setActualPrice(basePrice.multiply(request.getPriceModifier()));
        }

        if (request.getBusId() != null) {
            Bus bus = busRepository.findById(request.getBusId())
                    .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Bus not found"));
            trip.setBus(bus);
        }

        if (request.getDriverId() != null) {
            User driver = userRepository.findById(request.getDriverId())
                    .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Driver not found"));
            if (driver.getRole() != UserRole.DRIVER) {
                throw new ApiException(ErrorCodeConstants.VALIDATION_ERROR, HttpStatus.BAD_REQUEST, "User is not a driver");
            }
            trip.setDriver(driver);
        }

        if (request.getDepartureTime() != null) {
            trip.setDepartureTime(request.getDepartureTime());
            if (trip.getRoute().getEstimatedDuration() != null) {
                trip.setArrivalTime(request.getDepartureTime().plus(Duration.ofMinutes(trip.getRoute().getEstimatedDuration())));
            }
        }

        Trip saved = tripRepository.save(trip);
        return new TripResponse(
                saved.getId(),
                saved.getRoute().getOrigin().getName() + " - " + saved.getRoute().getDestination().getName(),
                saved.getBus().getLicensePlate(),
                saved.getDepartureTime(),
                saved.getActualPrice());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripSearchResponse> searchTrips(Long originProvinceId, Long destinationProvinceId, LocalDate date, Integer maxLegs, Integer minLayoverMinutes) {
        Instant startOfDay = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        List<TripSearchResponse> results = new ArrayList<>();

        // Quét tất cả chuyến xuất phát từ Tỉnh A
        List<Trip> allTripsFromA = tripRepository.findByOriginProvinceAndDepartureTimeBetween(
                originProvinceId, startOfDay, endOfDay);

        int layover = minLayoverMinutes != null ? minLayoverMinutes : 45;

        for (Trip trip1 : allTripsFromA) {
            Long currentProvId = trip1.getRoute().getDestination().getProvince().getId();
            
            // TRƯỜNG HỢP 1: CHUYẾN ĐI THẲNG (Điểm đến của Chuyến 1 trùng với Tỉnh đích B)
            if (currentProvId.equals(destinationProvinceId)) {
                TripLegResponse leg = createLegResponse(trip1);
                results.add(new TripSearchResponse("DIRECT", trip1.getActualPrice(), null, false, Arrays.asList(leg)));
                continue;
            }

            // TRƯỜNG HỢP 2: TÌM CHUYẾN NỐI (Tại Tỉnh trung chuyển)
            if (maxLegs != null && maxLegs >= 2) {
                if (trip1.getArrivalTime() == null) continue;

                // Layover Constraint: min 45m (or provided) to max 48 hours
                Instant minDepartureParams = trip1.getArrivalTime().plus(Duration.ofMinutes(layover));
                Instant maxDepartureParams = trip1.getArrivalTime().plus(Duration.ofHours(48));

                List<Trip> possibleTrip2s = tripRepository.findByOriginProvinceAndDestinationProvinceAndDepartureTimeBetween(
                        currentProvId, destinationProvinceId, minDepartureParams, maxDepartureParams);

                for (Trip trip2 : possibleTrip2s) {
                    boolean requiresStationTransfer = !trip1.getRoute().getDestination().getId()
                            .equals(trip2.getRoute().getOrigin().getId());

                    long layoverDuration = Duration.between(trip1.getArrivalTime(), trip2.getDepartureTime()).toMinutes();
                    long hours = layoverDuration / 60;
                    long minutes = layoverDuration % 60;
                    String layoverStr = hours > 0 ? hours + "h " + minutes + "m" : minutes + "m";

                    BigDecimal totalPrice = trip1.getActualPrice().add(trip2.getActualPrice());

                    TripLegResponse leg1 = createLegResponse(trip1);
                    TripLegResponse leg2 = createLegResponse(trip2);

                    results.add(new TripSearchResponse("CONNECTING", totalPrice, layoverStr, requiresStationTransfer, Arrays.asList(leg1, leg2)));
                }
            }
        }

        results.sort(Comparator.comparing(TripSearchResponse::getTotalPrice));
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getSeatMap(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Trip not found"));

        if (trip.getBus() == null) {
            throw new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, "Trip does not have an assigned bus");
        }

        List<Seat> busSeats = seatRepository.findByBusId(trip.getBus().getId());
        List<Ticket> activeTickets = ticketRepository.findByTripIdAndTicketStatus(tripId, TicketStatus.ACTIVE);
        List<SeatHold> activeHolds = seatHoldRepository.findByTripIdAndHoldStatusAndExpiresAtAfter(tripId, HoldStatus.HOLDING, Instant.now());

        Map<String, String> seatMap = new HashMap<>();

        // 1. Initial status: AVAILABLE
        for (Seat seat : busSeats) {
            seatMap.put(seat.getSeatNumber(), "AVAILABLE");
        }

        // 2. Mark HELD (if it was AVAILABLE)
        for (SeatHold hold : activeHolds) {
            seatMap.put(hold.getSeatNumber(), "HELD");
        }

        // 3. Mark BOOKED (always has priority)
        for (Ticket ticket : activeTickets) {
            seatMap.put(ticket.getSeatNumber(), "BOOKED");
        }

        return seatMap;
    }

    private TripLegResponse createLegResponse(Trip trip) {
        long bookedCount = ticketRepository.countByTripIdAndTicketStatus(trip.getId(), TicketStatus.ACTIVE);
        long heldCount = seatHoldRepository.countByTripIdAndHoldStatusAndExpiresAtAfter(trip.getId(), HoldStatus.HOLDING, Instant.now());
        int availableSeats = trip.getBus().getTotalSeats() - (int) bookedCount - (int) heldCount;

        return new TripLegResponse(
                trip.getId(),
                trip.getRoute().getOrigin().getName(),
                trip.getRoute().getDestination().getName(),
                trip.getDepartureTime(),
                trip.getArrivalTime(),
                trip.getBus().getBusType() != null ? trip.getBus().getBusType() : "Standard",
                availableSeats
        );
    }
}

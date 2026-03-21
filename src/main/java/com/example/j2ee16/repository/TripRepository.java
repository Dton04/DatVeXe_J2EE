package com.example.j2ee16.repository;

import com.example.j2ee16.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    
    List<Trip> findByRouteOriginIdAndRouteDestinationIdAndDepartureTimeBetween(
            Long originId, Long destinationId, Instant start, Instant end);

    List<Trip> findByRouteOriginIdAndRouteDestinationIdNotAndDepartureTimeBetween(
            Long originId, Long excludedDestinationId, Instant start, Instant end);

    List<Trip> findByRouteOriginIdAndRouteDestinationIdAndDepartureTimeGreaterThanEqual(
            Long originId, Long destinationId, Instant earliestDeparture);
}

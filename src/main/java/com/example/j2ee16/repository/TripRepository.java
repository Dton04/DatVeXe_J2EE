package com.example.j2ee16.repository;

import com.example.j2ee16.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    
    @Query("SELECT t FROM Trip t JOIN t.route r WHERE r.origin.province.id = :originProvinceId AND r.destination.province.id = :destinationProvinceId AND t.departureTime BETWEEN :start AND :end")
    List<Trip> findByOriginProvinceAndDestinationProvinceAndDepartureTimeBetween(
            @Param("originProvinceId") Long originProvinceId, @Param("destinationProvinceId") Long destinationProvinceId, @Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT t FROM Trip t JOIN t.route r WHERE r.origin.province.id = :originProvinceId AND t.departureTime BETWEEN :start AND :end")
    List<Trip> findByOriginProvinceAndDepartureTimeBetween(
            @Param("originProvinceId") Long originProvinceId, @Param("start") Instant start, @Param("end") Instant end);

    boolean existsByRouteId(Long routeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Trip t WHERE t.id IN :tripIds ORDER BY t.id ASC")
    List<Trip> findByIdInWithLock(@Param("tripIds") List<Long> tripIds);
}

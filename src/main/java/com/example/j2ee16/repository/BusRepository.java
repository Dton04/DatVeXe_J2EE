package com.example.j2ee16.repository;

import com.example.j2ee16.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    Optional<Bus> findByLicensePlate(String licensePlate);
    boolean existsByLicensePlate(String licensePlate);
}

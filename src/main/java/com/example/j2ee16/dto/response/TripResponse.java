package com.example.j2ee16.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public class TripResponse {
    private Long id;
    @JsonProperty("route_name")
    private String routeName;
    @JsonProperty("bus_plate")
    private String busPlate;
    @JsonProperty("bus_type")
    private String busType;
    @JsonProperty("total_seats")
    private Integer totalSeats;
    @JsonProperty("departure_time")
    private Instant departureTime;
    @JsonProperty("arrival_time")
    private Instant arrivalTime;
    @JsonProperty("actual_price")
    private BigDecimal actualPrice;
    private String status;
    @JsonProperty("driver_id")
    private Long driverId;
    @JsonProperty("driver_name")
    private String driverName;

    public TripResponse() {
    }

    public TripResponse(Long id, String routeName, String busPlate, Instant departureTime, BigDecimal actualPrice) {
        this.id = id;
        this.routeName = routeName;
        this.busPlate = busPlate;
        this.departureTime = departureTime;
        this.actualPrice = actualPrice;
    }

    public TripResponse(Long id, String routeName, String busPlate, String busType, Integer totalSeats,
                        Instant departureTime, Instant arrivalTime, BigDecimal actualPrice, String status,
                        Long driverId, String driverName) {
        this.id = id;
        this.routeName = routeName;
        this.busPlate = busPlate;
        this.busType = busType;
        this.totalSeats = totalSeats;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.actualPrice = actualPrice;
        this.status = status;
        this.driverId = driverId;
        this.driverName = driverName;
    }

    public TripResponse(Long id, String routeName, String busPlate, String busType, Integer totalSeats,
                        Instant departureTime, Instant arrivalTime, BigDecimal actualPrice, String status) {
        this(id, routeName, busPlate, busType, totalSeats, departureTime, arrivalTime, actualPrice, status, null, null);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public String getBusPlate() { return busPlate; }
    public void setBusPlate(String busPlate) { this.busPlate = busPlate; }

    public String getBusType() { return busType; }
    public void setBusType(String busType) { this.busType = busType; }

    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }

    public Instant getDepartureTime() { return departureTime; }
    public void setDepartureTime(Instant departureTime) { this.departureTime = departureTime; }

    public Instant getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(Instant arrivalTime) { this.arrivalTime = arrivalTime; }

    public BigDecimal getActualPrice() { return actualPrice; }
    public void setActualPrice(BigDecimal actualPrice) { this.actualPrice = actualPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
}

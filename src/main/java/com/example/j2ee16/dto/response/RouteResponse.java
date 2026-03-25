package com.example.j2ee16.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class RouteResponse {
    private Long id;
    private String name;
    private BigDecimal price;

    @JsonProperty("departure")
    private String departure;

    @JsonProperty("destination")
    private String destination;

    @JsonProperty("distance")
    private Double distance;

    @JsonProperty("duration")
    private Integer duration;

    @JsonProperty("departure_date")
    private java.time.LocalDate departureDate;

    @JsonProperty("origin_station")
    private StationResponse originStation;

    @JsonProperty("destination_station")
    private StationResponse destinationStation;

    public RouteResponse() {
    }

    public RouteResponse(Long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public java.time.LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(java.time.LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public StationResponse getOriginStation() {
        return originStation;
    }

    public void setOriginStation(StationResponse originStation) {
        this.originStation = originStation;
    }

    public StationResponse getDestinationStation() {
        return destinationStation;
    }

    public void setDestinationStation(StationResponse destinationStation) {
        this.destinationStation = destinationStation;
    }
}

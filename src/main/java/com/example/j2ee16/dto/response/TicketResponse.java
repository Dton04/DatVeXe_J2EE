package com.example.j2ee16.dto.response;

import com.example.j2ee16.entity.CheckInStatus;
import com.example.j2ee16.entity.TicketStatus;

import lombok.Data;


import java.math.BigDecimal;

@Data
public class TicketResponse {
    private Long id;
    @com.fasterxml.jackson.annotation.JsonProperty("ticket_code")
    private String ticketCode;
    @com.fasterxml.jackson.annotation.JsonProperty("seat_number")
    private String seatNumber;
    @com.fasterxml.jackson.annotation.JsonProperty("passenger_name")
    private String passengerName;
    private String phone;
    private BigDecimal price;
    @com.fasterxml.jackson.annotation.JsonProperty("ticket_status")
    private TicketStatus ticketStatus;
    @com.fasterxml.jackson.annotation.JsonProperty("check_in_status")
    private CheckInStatus checkInStatus;

    public TicketResponse() {
    }

    public TicketResponse(Long id, String ticketCode, String seatNumber, String passengerName, String phone, BigDecimal price, TicketStatus ticketStatus, CheckInStatus checkInStatus) {
        this.id = id;
        this.ticketCode = ticketCode;
        this.seatNumber = seatNumber;
        this.passengerName = passengerName;
        this.phone = phone;
        this.price = price;
        this.ticketStatus = ticketStatus;
        this.checkInStatus = checkInStatus;
    }
}

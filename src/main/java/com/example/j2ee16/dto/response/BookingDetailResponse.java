package com.example.j2ee16.dto.response;

import com.example.j2ee16.entity.BookingStatus;
import com.example.j2ee16.entity.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class BookingDetailResponse {
    @JsonProperty("booking_info")
    private BookingInfo bookingInfo;

    @JsonProperty("trip_info")
    private TripInfo tripInfo;

    private List<TicketItem> tickets;

    private PaymentInfo payment;

    @JsonProperty("qr_string")
    private String qrString;

    private String policies;

    public BookingDetailResponse(BookingInfo bookingInfo, TripInfo tripInfo, List<TicketItem> tickets, PaymentInfo payment, String qrString, String policies) {
        this.bookingInfo = bookingInfo;
        this.tripInfo = tripInfo;
        this.tickets = tickets;
        this.payment = payment;
        this.qrString = qrString;
        this.policies = policies;
    }

    public BookingInfo getBookingInfo() {
        return bookingInfo;
    }

    public TripInfo getTripInfo() {
        return tripInfo;
    }

    public List<TicketItem> getTickets() {
        return tickets;
    }

    public PaymentInfo getPayment() {
        return payment;
    }

    public String getQrString() {
        return qrString;
    }

    public String getPolicies() {
        return policies;
    }

    public static class BookingInfo {
        @JsonProperty("booking_code")
        private String bookingCode;
        private BookingStatus status;
        @JsonProperty("created_at")
        private Instant createdAt;

        public BookingInfo(String bookingCode, BookingStatus status, Instant createdAt) {
            this.bookingCode = bookingCode;
            this.status = status;
            this.createdAt = createdAt;
        }

        public String getBookingCode() {
            return bookingCode;
        }

        public BookingStatus getStatus() {
            return status;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }
    }

    public static class TripInfo {
        private String route;
        @JsonProperty("bus_plate")
        private String busPlate;
        @JsonProperty("bus_type")
        private String busType;
        private String departure;
        @JsonProperty("pickup_point")
        private String pickupPoint;

        public TripInfo(String route, String busPlate, String busType, String departure, String pickupPoint) {
            this.route = route;
            this.busPlate = busPlate;
            this.busType = busType;
            this.departure = departure;
            this.pickupPoint = pickupPoint;
        }

        public String getRoute() {
            return route;
        }

        public String getBusPlate() {
            return busPlate;
        }

        public String getBusType() {
            return busType;
        }

        public String getDeparture() {
            return departure;
        }

        public String getPickupPoint() {
            return pickupPoint;
        }
    }

    public static class TicketItem {
        private String seat;
        @JsonProperty("passenger")
        private String passenger;

        public TicketItem(String seat, String passenger) {
            this.seat = seat;
            this.passenger = passenger;
        }

        public String getSeat() {
            return seat;
        }

        public String getPassenger() {
            return passenger;
        }
    }

    public static class PaymentInfo {
        private String method;
        private BigDecimal total;
        @JsonProperty("status")
        private PaymentStatus status;

        public PaymentInfo(String method, BigDecimal total, PaymentStatus status) {
            this.method = method;
            this.total = total;
            this.status = status;
        }

        public String getMethod() {
            return method;
        }

        public BigDecimal getTotal() {
            return total;
        }

        public PaymentStatus getStatus() {
            return status;
        }
    }
}


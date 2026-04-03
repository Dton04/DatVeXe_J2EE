package com.example.j2ee16.service;

public interface EmailService {
    void sendReminderEmail(String to, String passengerName, String routeName, String departureTime, String seatNumber, String ticketCode);
    void sendPaymentSuccessEmail(String to, String customerName, String bookingCode, java.math.BigDecimal totalAmount);
}

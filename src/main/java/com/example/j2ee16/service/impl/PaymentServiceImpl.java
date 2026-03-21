package com.example.j2ee16.service.impl;

import com.example.j2ee16.constants.ErrorCodeConstants;
import com.example.j2ee16.dto.request.PaymentRequest;
import com.example.j2ee16.dto.response.PaymentResponse;
import com.example.j2ee16.entity.*;
import com.example.j2ee16.exception.ApiException;
import com.example.j2ee16.repository.*;
import com.example.j2ee16.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final SeatHoldRepository seatHoldRepository;
    private final TicketRepository ticketRepository;
    private final com.example.j2ee16.config.VNPayConfig vnPayConfig;

    public PaymentServiceImpl(BookingRepository bookingRepository, PaymentRepository paymentRepository,
                              SeatHoldRepository seatHoldRepository, TicketRepository ticketRepository,
                              com.example.j2ee16.config.VNPayConfig vnPayConfig) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.seatHoldRepository = seatHoldRepository;
        this.ticketRepository = ticketRepository;
        this.vnPayConfig = vnPayConfig;
    }

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Booking not found"));

        if (booking.getBookingStatus() == BookingStatus.EXPIRED || booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.BAD_REQUEST, "Booking is no longer valid for payment.");
        }

        if (booking.getPaymentStatus() == PaymentStatus.PAID) {
            throw new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.BAD_REQUEST, "Booking is already paid.");
        }

        // Generate Transaction Ref
        String transactionRef = "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setTransactionRef(transactionRef);
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.UNPAID);

        paymentRepository.save(payment);

        booking.setBookingStatus(BookingStatus.PENDING_PAYMENT);
        bookingRepository.save(booking);

        // Build VNPAY URL
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnPayConfig.getVnpTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(booking.getTotalAmount().multiply(new BigDecimal(100)).longValue()));
        vnp_Params.put("vnp_CurrCode", "VND");
        
        // Need to add timezone logic. Using simple pattern here
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(java.time.ZoneId.of("GMT+7"));
        String vnp_CreateDate = formatter.format(java.time.Instant.now());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // Optional/Default parameters
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang " + transactionRef);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getVnpReturnUrl());
        vnp_Params.put("vnp_TxnRef", transactionRef);

        // Calculate Hash
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                try {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(java.net.URLEncoder.encode(fieldValue, java.nio.charset.StandardCharsets.US_ASCII.toString()));
                    query.append(java.net.URLEncoder.encode(fieldName, java.nio.charset.StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(java.net.URLEncoder.encode(fieldValue, java.nio.charset.StandardCharsets.US_ASCII.toString()));
                } catch (java.io.UnsupportedEncodingException e) {
                   // Ignore
                }
                
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        
        String queryUrl = query.toString();
        String vnp_SecureHash = vnPayConfig.hmacSHA512(vnPayConfig.getVnpHashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnPayConfig.getVnpUrl() + "?" + queryUrl;

        return new PaymentResponse(paymentUrl, transactionRef);
    }

    @Override
    @Transactional
    public void handlePaymentCallback(Map<String, String> params) {
        String vnp_SecureHash = params.get("vnp_SecureHash");
        if (vnp_SecureHash != null) {
            params.remove("vnp_SecureHashType");
            params.remove("vnp_SecureHash");
            
            // Re-hash and compare
            String signValue = vnPayConfig.hashAllFields(params);
            if (!signValue.equals(vnp_SecureHash)) {
                throw new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.BAD_REQUEST, "Invalid Signature");
            }
        } else {
             throw new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.BAD_REQUEST, "Missing Signature");
        }

        String transactionRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String status = "00".equals(responseCode) ? "SUCCESS" : "FAILED";

        Payment payment = paymentRepository.findByTransactionRef(transactionRef)
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Payment transaction not found"));

        if (payment.getStatus() == PaymentStatus.PAID) {
            return; // Already processed
        }

        Booking booking = payment.getBooking();

        if ("SUCCESS".equalsIgnoreCase(status)) {
            payment.setStatus(PaymentStatus.PAID);
            booking.setPaymentStatus(PaymentStatus.PAID);
            booking.setBookingStatus(BookingStatus.CONFIRMED);

            // Fetch holds and convert to tickets
            List<SeatHold> holds = seatHoldRepository.findByBookingId(booking.getId());
            for (SeatHold hold : holds) {
                hold.setHoldStatus(HoldStatus.CONFIRMED);
                seatHoldRepository.save(hold);

                Ticket ticket = new Ticket();
                ticket.setBooking(booking);
                ticket.setTrip(hold.getTrip());
                ticket.setSeatNumber(hold.getSeatNumber());
                ticket.setPassengerName(booking.getCustomerName());
                ticket.setPhone(booking.getCustomerPhone());
                ticket.setPrice(hold.getTrip().getActualPrice());
                ticket.setTicketStatus(TicketStatus.ACTIVE);
                ticket.setCheckInStatus(CheckInStatus.NOT_YET);

                ticketRepository.save(ticket);
            }
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            // We don't expire the booking immediately, it stays PENDING_PAYMENT until the cron job expires the hold.
        }

        paymentRepository.save(payment);
        bookingRepository.save(booking);
    }
}

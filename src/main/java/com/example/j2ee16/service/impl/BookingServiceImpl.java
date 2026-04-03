package com.example.j2ee16.service.impl;

import com.example.j2ee16.constants.ErrorCodeConstants;
import com.example.j2ee16.dto.request.BookingLegRequest;
import com.example.j2ee16.dto.request.BookingRequest;
import com.example.j2ee16.dto.response.BookingResponse;
import com.example.j2ee16.dto.response.BookingDetailResponse;
import com.example.j2ee16.dto.response.MyBookingResponse;
import com.example.j2ee16.entity.*;
import com.example.j2ee16.exception.ApiException;
import com.example.j2ee16.repository.*;
import com.example.j2ee16.service.BookingService;
import com.example.j2ee16.constants.PolicyConstants;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final SeatHoldRepository seatHoldRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TripStopRepository tripStopRepository;
    private final PaymentRepository paymentRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, TripRepository tripRepository,
            SeatHoldRepository seatHoldRepository, TicketRepository ticketRepository,
            UserRepository userRepository, TripStopRepository tripStopRepository, PaymentRepository paymentRepository) {
        this.bookingRepository = bookingRepository;
        this.tripRepository = tripRepository;
        this.seatHoldRepository = seatHoldRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.tripStopRepository = tripStopRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request, String username) {
        // 1. Resolve User (if authenticated)
        User user = null;
        if (username != null) {
            user = userRepository.findByEmail(username).orElse(null);
        }

        // 2. Extract and Lock Trips to prevent Deadlock (sorted by ID)
        List<Long> tripIds = request.getLegs().stream()
                .map(BookingLegRequest::getTripId)
                .distinct()
                .sorted()
                .toList();

        List<Trip> trips = tripRepository.findByIdInWithLock(tripIds);
        if (trips.size() != tripIds.size()) {
            throw new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, 
                    "One or more trips not found");
        }

        Map<Long, Trip> tripMap = new HashMap<>();
        for (Trip trip : trips) {
            tripMap.put(trip.getId(), trip);
        }

        // 3. Validate availability and calculate total price
        BigDecimal totalAmount = BigDecimal.ZERO;
        Instant now = Instant.now();

        for (BookingLegRequest leg : request.getLegs()) {
            Trip trip = tripMap.get(leg.getTripId());
            if (trip == null) {
                throw new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND,
                        "Trip not found: " + leg.getTripId());
            }

            // Check if seat is currently BOOKED
            List<Ticket> tickets = ticketRepository.findByTripIdAndTicketStatus(trip.getId(), TicketStatus.ACTIVE);
            boolean isBooked = tickets.stream().anyMatch(t -> t.getSeatNumber().equals(leg.getSeatNumber()));
            if (isBooked) {
                throw new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.CONFLICT,
                        "Seat " + leg.getSeatNumber() + " is already booked for trip " + trip.getId());
            }

            // Check if seat is currently HELD
            List<SeatHold> holds = seatHoldRepository.findByTripIdAndHoldStatusAndExpiresAtAfter(trip.getId(),
                    HoldStatus.HOLDING, now);
            boolean isHeld = holds.stream().anyMatch(h -> h.getSeatNumber().equals(leg.getSeatNumber()));
            if (isHeld) {
                throw new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.CONFLICT,
                        "Seat " + leg.getSeatNumber() + " is currently held by someone else for trip " + trip.getId());
            }

            BigDecimal legPrice = trip.getActualPrice() != null ? trip.getActualPrice()
                    : (trip.getRoute() != null ? trip.getRoute().getBasePrice() : null);
            if (legPrice == null) {
                throw new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.CONFLICT,
                        "Price not configured for trip " + trip.getId());
            }
            totalAmount = totalAmount.add(legPrice);
        }

        // 4. Create Booking
        Booking booking = new Booking();
        booking.setBookingCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        booking.setUser(user);
        booking.setCustomerName(request.getCustomerName());
        booking.setCustomerPhone(request.getCustomerPhone());
        booking.setTotalAmount(totalAmount);
        booking.setBookingStatus(BookingStatus.HOLDING);
        booking.setPaymentStatus(PaymentStatus.UNPAID);
        booking.setHoldExpiresAt(now.plus(10, ChronoUnit.MINUTES));

        Booking savedBooking = bookingRepository.save(booking);

        // 5. Create SeatHolds
        for (BookingLegRequest leg : request.getLegs()) {
            Trip trip = tripMap.get(leg.getTripId());

            SeatHold hold = new SeatHold();
            hold.setTrip(trip);
            hold.setBooking(savedBooking);
            hold.setSeatNumber(leg.getSeatNumber());
            hold.setHoldStatus(HoldStatus.HOLDING);
            hold.setExpiresAt(savedBooking.getHoldExpiresAt());

            try {
                seatHoldRepository.save(hold);
            } catch (DataIntegrityViolationException ex) {
                throw new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.CONFLICT,
                        "Seat " + leg.getSeatNumber() + " is currently held by someone else for trip " + trip.getId());
            }
        }

        return new BookingResponse(
                savedBooking.getId(),
                savedBooking.getBookingCode(),
                savedBooking.getTotalAmount(),
                savedBooking.getBookingStatus(),
                savedBooking.getHoldExpiresAt());
    }

    @Override
    @Transactional
    public void confirmCashPayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND,
                        "Booking not found"));

        if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            return;
        }

        if (booking.getBookingStatus() == BookingStatus.EXPIRED
                || booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.BAD_REQUEST,
                    "Booking is no longer valid.");
        }

        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        completeBooking(bookingId);
    }

    @Override
    @Transactional
    public void completeBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        List<SeatHold> holds = seatHoldRepository.findByBookingId(bookingId);
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
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND,
                        "Booking not found"));

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            return;
        }

        // 1. Check regulation: 24h before departure
        List<SeatHold> holds = seatHoldRepository.findByBookingId(bookingId);
        Instant earliestDeparture = null;
        for (SeatHold hold : holds) {
            Instant depTime = hold.getTrip().getDepartureTime();
            if (earliestDeparture == null || depTime.isBefore(earliestDeparture)) {
                earliestDeparture = depTime;
            }
        }

        if (earliestDeparture != null) {
            long hoursToDeparture = ChronoUnit.HOURS.between(Instant.now(), earliestDeparture);
            if (hoursToDeparture < 24) {
                throw new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.BAD_REQUEST,
                        "Cannot cancel booking less than 24h before departure.");
            }
        }

        // 2. Update Booking Status
        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // 3. Update Tickets/Holds
        for (SeatHold hold : holds) {
            hold.setHoldStatus(HoldStatus.RELEASED);
            seatHoldRepository.save(hold);
        }

        List<Ticket> tickets = ticketRepository.findAll(); // Optimization: should find by booking
        for (Ticket ticket : tickets) {
            if (ticket.getBooking() != null && ticket.getBooking().getId().equals(bookingId)) {
                ticket.setTicketStatus(TicketStatus.CANCELLED);
                ticketRepository.save(ticket);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyBookingResponse> getMyBookings(String email, String type) {
        if (email == null || email.isBlank()) {
            throw new ApiException(ErrorCodeConstants.UNAUTHORIZED, HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String normalizedType = type == null ? "UPCOMING" : type.trim().toUpperCase(Locale.ROOT);
        boolean isUpcoming = "UPCOMING".equals(normalizedType);
        boolean isHistory = "HISTORY".equals(normalizedType);
        boolean isHolding = "HOLDING".equals(normalizedType);
        boolean isPendingPayment = "PENDING_PAYMENT".equals(normalizedType);
        boolean isAll = "ALL".equals(normalizedType);
        if (!(isUpcoming || isHistory || isHolding || isPendingPayment || isAll)) {
            throw new ApiException(ErrorCodeConstants.VALIDATION_ERROR, HttpStatus.BAD_REQUEST, "Invalid type");
        }

        List<Booking> bookings = bookingRepository.findByUserEmail(email);
        if (bookings.isEmpty()) {
            return List.of();
        }

        List<Long> bookingIds = bookings.stream().map(Booking::getId).toList();
        List<Ticket> tickets = ticketRepository.findByBookingIdIn(bookingIds);

        Map<Long, List<Ticket>> ticketsByBookingId = new HashMap<>();
        for (Ticket ticket : tickets) {
            if (ticket.getBooking() == null || ticket.getBooking().getId() == null) {
                continue;
            }
            ticketsByBookingId.computeIfAbsent(ticket.getBooking().getId(), ignored -> new ArrayList<>()).add(ticket);
        }

        Instant now = Instant.now();
        List<MyBookingResponse> responses = new ArrayList<>();

        for (Booking booking : bookings) {
            List<Ticket> bookingTickets = ticketsByBookingId.getOrDefault(booking.getId(), List.of());

            String routeName = null;
            Instant departureTime = null;
            int seatCount = bookingTickets.size();

            if (!bookingTickets.isEmpty()) {
                List<Ticket> sortedTickets = new ArrayList<>(bookingTickets);
                sortedTickets.sort(Comparator.comparing(ticket -> ticket.getTrip().getDepartureTime()));

                Trip firstTrip = sortedTickets.get(0).getTrip();
                Trip lastTrip = sortedTickets.get(sortedTickets.size() - 1).getTrip();

                departureTime = firstTrip.getDepartureTime();
                routeName = firstTrip.getRoute().getOrigin().getName() + " - " + lastTrip.getRoute().getDestination().getName();
            } else {
                List<SeatHold> holds = seatHoldRepository.findByBookingId(booking.getId());
                if (!holds.isEmpty()) {
                    holds.sort(Comparator.comparing(hold -> hold.getTrip().getDepartureTime()));
                    Trip firstTrip = holds.get(0).getTrip();
                    Trip lastTrip = holds.get(holds.size() - 1).getTrip();
                    departureTime = firstTrip.getDepartureTime();
                    routeName = firstTrip.getRoute().getOrigin().getName() + " - " + lastTrip.getRoute().getDestination().getName();
                    seatCount = holds.size();
                }
            }

            boolean cancelledOrExpired = booking.getBookingStatus() == BookingStatus.CANCELLED
                    || booking.getBookingStatus() == BookingStatus.EXPIRED;

            boolean timeBasedUpcoming = departureTime != null && departureTime.isAfter(now);
            boolean timeBasedHistory = departureTime != null && !departureTime.isAfter(now);

            boolean include = (isUpcoming && timeBasedUpcoming && !cancelledOrExpired)
                    || (isHistory && (cancelledOrExpired || timeBasedHistory))
                    || (isHolding && booking.getBookingStatus() == BookingStatus.HOLDING)
                    || (isPendingPayment && booking.getBookingStatus() == BookingStatus.PENDING_PAYMENT)
                    || (isAll);

            if (!include) {
                continue;
            }

            responses.add(new MyBookingResponse(
                    booking.getId(),
                    booking.getBookingCode(),
                    routeName,
                    departureTime,
                    booking.getTotalAmount(),
                    seatCount,
                    booking.getBookingStatus(),
                    booking.getPaymentStatus()
            ));
        }

        Comparator<MyBookingResponse> departureComparator = Comparator.comparing(
                MyBookingResponse::getDepartureTime,
                Comparator.nullsLast(Comparator.naturalOrder())
        );
        responses.sort(isUpcoming ? departureComparator : departureComparator.reversed());

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDetailResponse getBookingDetail(Long bookingId, String email) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Booking not found"));

        if (booking.getUser() == null || booking.getUser().getEmail() == null || !booking.getUser().getEmail().equals(email)) {
            throw new ApiException(ErrorCodeConstants.FORBIDDEN, HttpStatus.FORBIDDEN, "Forbidden");
        }

        List<Ticket> tickets = ticketRepository.findByBookingId(bookingId);
        if (tickets.isEmpty()) {
            List<SeatHold> holds = seatHoldRepository.findByBookingId(bookingId);
            if (holds.isEmpty()) {
                throw new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "No tickets found");
            }
        }

        Trip mainTrip = null;
        if (!tickets.isEmpty()) {
            tickets.sort(Comparator.comparing(t -> t.getTrip().getDepartureTime()));
            mainTrip = tickets.get(0).getTrip();
        } else {
            List<SeatHold> holds = seatHoldRepository.findByBookingId(bookingId);
            holds.sort(Comparator.comparing(h -> h.getTrip().getDepartureTime()));
            mainTrip = holds.get(0).getTrip();
        }

        String route = mainTrip.getRoute().getOrigin().getName() + " -> " + mainTrip.getRoute().getDestination().getName();
        String busPlate = mainTrip.getBus().getLicensePlate();
        String busType = mainTrip.getBus().getBusType() != null ? mainTrip.getBus().getBusType() : "Standard";

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy").withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
        String departureStr = fmt.format(mainTrip.getDepartureTime());

        List<TripStop> stops = tripStopRepository.findByTripIdOrderByOrderIndexAsc(mainTrip.getId());
        String pickupPoint = "N/A";
        for (TripStop s : stops) {
            if (s.getStopType() == StopType.PICKUP) {
                pickupPoint = s.getStation().getName();
                break;
            }
        }

        List<BookingDetailResponse.TicketItem> ticketItems = new ArrayList<>();
        List<String> seatNumbers = new ArrayList<>();
        for (Ticket t : tickets) {
            ticketItems.add(new BookingDetailResponse.TicketItem(t.getSeatNumber(), t.getPassengerName(), t.getTicketCode()));
            seatNumbers.add(t.getSeatNumber());
        }
        String seatsJoined = String.join(",", seatNumbers);
        String qr = booking.getBookingCode() + "|" + seatsJoined + "|" + booking.getBookingStatus().name();

        Payment payment = paymentRepository.findFirstByBookingIdOrderByCreatedAtDesc(bookingId).orElse(null);
        BookingDetailResponse.PaymentInfo paymentInfo = null;
        if (payment != null) {
            paymentInfo = new BookingDetailResponse.PaymentInfo(payment.getPaymentMethod(), booking.getTotalAmount(), payment.getStatus());
        } else {
            paymentInfo = new BookingDetailResponse.PaymentInfo("N/A", booking.getTotalAmount(), booking.getPaymentStatus());
        }

        BookingDetailResponse.BookingInfo bookingInfo = new BookingDetailResponse.BookingInfo(
                booking.getBookingCode(),
                booking.getBookingStatus(),
                booking.getCreatedAt() != null ? booking.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant() : null
        );
        BookingDetailResponse.TripInfo tripInfo = new BookingDetailResponse.TripInfo(
                route,
                busPlate,
                busType,
                departureStr,
                pickupPoint
        );

        return new BookingDetailResponse(
                bookingInfo,
                tripInfo,
                ticketItems,
                paymentInfo,
                qr,
                PolicyConstants.ETICKET_POLICIES
        );
    }

    @Override
    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void cancelExpiredHolds() {
        Instant now = Instant.now();
        List<Booking> expiredBookings = bookingRepository.findByBookingStatusInAndHoldExpiresAtBefore(
                List.of(BookingStatus.HOLDING, BookingStatus.PENDING_PAYMENT),
                now
        );

        for (Booking booking : expiredBookings) {
            booking.setBookingStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);
            List<SeatHold> holds = seatHoldRepository.findByBookingId(booking.getId());
            for (SeatHold hold : holds) {
                boolean shouldRelease = hold.getHoldStatus() == HoldStatus.HOLDING
                        && (hold.getExpiresAt() == null || !hold.getExpiresAt().isAfter(now));
                if (shouldRelease) {
                    hold.setHoldStatus(HoldStatus.RELEASED);
                    seatHoldRepository.save(hold);
                }
            }
        }
    }
}

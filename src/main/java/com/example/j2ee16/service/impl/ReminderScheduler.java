package com.example.j2ee16.service.impl;

import com.example.j2ee16.entity.Ticket;
import com.example.j2ee16.entity.TicketStatus;
import com.example.j2ee16.entity.User;
import com.example.j2ee16.repository.TicketRepository;
import com.example.j2ee16.service.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReminderScheduler {

    private final TicketRepository ticketRepository;
    private final EmailService emailService;

    public ReminderScheduler(TicketRepository ticketRepository, EmailService emailService) {
        this.ticketRepository = ticketRepository;
        this.emailService = emailService;
    }

    // Run every minute for testing. For production, maybe every 15-30 minutes.
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void sendEmailReminders() {
        Instant now = Instant.now();
        // Look ahead exactly 24 hours. To catch them, we scan window from (now + 23h50m) to (now + 24h)
        Instant start = now.plus(23, ChronoUnit.HOURS).plus(50, ChronoUnit.MINUTES);
        Instant end = now.plus(24, ChronoUnit.HOURS).plus(5, ChronoUnit.MINUTES);

        List<Ticket> upcomingTickets = ticketRepository.findUpcomingTicketsForReminder(TicketStatus.ACTIVE, start, end);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy").withZone(ZoneId.of("Asia/Ho_Chi_Minh"));

        for (Ticket ticket : upcomingTickets) {
            System.out.println("Processing ticket for reminder: " + ticket.getTicketCode());
            // Email is only sent if we have an associated user with an email.
            if (ticket.getBooking() != null && ticket.getBooking().getUser() != null) {
                User user = ticket.getBooking().getUser();
                String email = user.getEmail();
                
                if (email != null && !email.isBlank()) {
                    String routeName = ticket.getTrip().getRoute().getOrigin().getName() + " - " + ticket.getTrip().getRoute().getDestination().getName();
                    String departureTime = fmt.format(ticket.getTrip().getDepartureTime());
                    
                    emailService.sendReminderEmail(
                        email, 
                        ticket.getPassengerName(), 
                        routeName, 
                        departureTime, 
                        ticket.getSeatNumber(), 
                        ticket.getTicketCode()
                    );
                }
            }
            
            // Mark as sent regardless if email was available to prevent spamming
            ticket.setReminderSent(true);
            ticketRepository.save(ticket);
        }
    }
}

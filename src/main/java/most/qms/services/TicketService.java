package most.qms.services;

import jakarta.persistence.EntityNotFoundException;
import most.qms.exceptions.VerificationException;
import most.qms.models.Group;
import most.qms.models.Ticket;
import most.qms.models.User;
import most.qms.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static java.time.LocalDateTime.now;
import static java.util.EnumSet.of;
import static most.qms.models.Status.*;

@Service
@Transactional(readOnly = true)
class TicketService {
    private final TicketRepository ticketRepo;
    private final UserService userService;
    private final GroupService groupService;
    private final DailyCounterService dailyCounterService;

    @Autowired
    TicketService(TicketRepository ticketRepo, UserService userService, GroupService groupService, DailyCounterService dailyCounterService) {
        this.ticketRepo = ticketRepo;
        this.userService = userService;
        this.groupService = groupService;
        this.dailyCounterService = dailyCounterService;
    }

    @Transactional
    public Ticket create(Long userId) {
        User user = userService.findByUserId(userId);
        if (!user.getIsPhoneVerified()) {
            throw new VerificationException("User with id=%d not verified!"
                    .formatted(userId));
        }

        Boolean hasActiveTickets = ticketRepo
                .existsByUserAndStatusIn(user,
                        of(WAITING, CALLED));

        if (hasActiveTickets) {
            throw new DataIntegrityViolationException(
                    "User with id=%d already has active ticket!"
                            .formatted(userId));
        }
        Group group = groupService.findLastAvailable();
        return ticketRepo.save(new Ticket(user, group, dailyCounterService.getAndIncrement()));
    }

    @Transactional
    public void cancel(Long ticketId) {
        Ticket ticket = ticketRepo
                .findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ticket with id=%d now found!"
                                .formatted(ticketId)));
        ticket.setStatus(CANCELED);
        ticketRepo.save(ticket);
    }


    @Transactional
    public void markAllAsWaitingInGroup(Group group) {
        Set<Ticket> tickets = group.getTickets();
        tickets
                .forEach(ticket -> ticket.setStatus(WAITING));
        ticketRepo.saveAll(tickets);
    }

    @Transactional
    public void markAllAsCompletedInGroup(Group group) {
        Set<Ticket> tickets = group.getTickets();
        tickets
                .forEach(ticket -> {
                    ticket.setCompletedAt(now());
                    ticket.setStatus(COMPLETE);
                });
        ticketRepo.saveAll(tickets);
    }
}

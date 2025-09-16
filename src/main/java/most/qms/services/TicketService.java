package most.qms.services;

import jakarta.persistence.EntityNotFoundException;
import most.qms.dtos.responses.TicketDto;
import most.qms.exceptions.VerificationException;
import most.qms.models.Group;
import most.qms.models.TicketStatus;
import most.qms.models.Ticket;
import most.qms.models.User;
import most.qms.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static java.time.LocalDateTime.now;
import static java.util.EnumSet.of;
import static most.qms.models.TicketStatus.*;

@Service
@Transactional(readOnly = true)
public class TicketService {
    private final TicketRepository ticketRepo;
    private final UserService userService;
    private final GroupService groupService;
    private final DailyCounterService dailyCounterService;
    private static final EnumSet<TicketStatus> ACTIVE_TICKET_STATUSES;

    static {
        ACTIVE_TICKET_STATUSES = of(WAITING, CALLED);
    }

    @Autowired
    TicketService(TicketRepository ticketRepo, UserService userService, GroupService groupService, DailyCounterService dailyCounterService) {
        this.ticketRepo = ticketRepo;
        this.userService = userService;
        this.groupService = groupService;
        this.dailyCounterService = dailyCounterService;
    }

    public List<TicketDto> findAll(){
        return ticketRepo.findAll().stream().map(this::convertToDto).toList();
    }

    @Transactional
    public TicketDto create(Long userId) {
        User user = userService.findEntityById(userId);
        if (!user.getIsPhoneVerified()) {
            throw new VerificationException("User with id=%d not verified!"
                    .formatted(userId));
        }

        System.err.println(user.getTickets());
        boolean hasActiveTickets = user
                .getTickets()
                .stream()
                .map(Ticket::getStatus)
                .anyMatch(ACTIVE_TICKET_STATUSES::contains);

        System.err.println(hasActiveTickets);
        if (hasActiveTickets) {
            throw new DataIntegrityViolationException(
                    "User with id=%d already has active ticket!"
                            .formatted(userId));
        }
        Group group = groupService.findLastAvailable();
        Long number = dailyCounterService.getAndIncrement();

        Ticket saved = ticketRepo.save(new Ticket(user, group, number));

        group.getTickets().add(saved);
        groupService.save(group);

        user.getTickets().add(saved);
        userService.save(user);

        return convertToDto(saved);
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

    private TicketDto convertToDto(Ticket entity){
        return new TicketDto(entity.getId(),
                entity.getNumber(),
                entity.getStatus(),
                entity.getCreatedAt());
    }
}

package most.qms.services;

import jakarta.persistence.EntityNotFoundException;
import most.qms.dtos.responses.CreatedTicketDto;
import most.qms.exceptions.EntityNotCreatedException;
import most.qms.models.Group;
import most.qms.models.Ticket;
import most.qms.models.TicketStatus;
import most.qms.repositories.TicketRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;

import static java.util.EnumSet.of;
import static most.qms.dtos.responses.CreatedTicketDto.from;
import static most.qms.models.TicketStatus.CALLED;
import static most.qms.models.TicketStatus.WAITING;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@Service
@Transactional(readOnly = true)
public class TicketService {
    private static final Logger log = getLogger(TicketService.class);
    private final TicketRepository repo;
    private final UserService userService;
    private final GroupCrudService groupCrud;
    private final AutoCallQueue autoCallQueue;
    private final ApplicationEventPublisher publisher;
    private static final EnumSet<TicketStatus> ACTIVE_TICKET_STATUSES;

    static {
        ACTIVE_TICKET_STATUSES = of(WAITING, CALLED);
    }

    @Autowired
    TicketService(TicketRepository repo,
                  UserService userService,
                  GroupCrudService groupCrud,
                  AutoCallQueue autoCallQueue,
                  ApplicationEventPublisher publisher) {
        this.repo = repo;
        this.userService = userService;
        this.groupCrud = groupCrud;
        this.autoCallQueue = autoCallQueue;
        this.publisher = publisher;
    }

    public List<CreatedTicketDto> findAll() {
        return repo.findAll().stream().map(CreatedTicketDto::from).toList();
    }

    @Transactional
    public ResponseEntity<CreatedTicketDto> create() {
        var user = userService.getUserFromContextAndVerify();

        boolean hasActiveTickets = user
                .getTickets()
                .stream()
                .map(Ticket::getStatus)
                .anyMatch(ACTIVE_TICKET_STATUSES::contains);

        if (hasActiveTickets) {
            throw new EntityNotCreatedException(
                    "User with phoneNumber=%s already has active ticket!"
                            .formatted(user.getPhoneNumber()));
        }

        Group group = groupCrud.getLastAvailable();

        Ticket toSave = new Ticket(user, group);
        user.addTicket(toSave);
        group.addTicket(toSave);

        Ticket saved = repo.save(toSave);
        return ok(from(saved));
    }

    @Transactional
    public ResponseEntity<CreatedTicketDto> markAsComplete() {
        var user = userService.getUserFromContextAndVerify();
        var ticket = repo
                .findActiveByUserId(user.getId())
                .orElseThrow(throwActiveTicketNotFound());

        ticket.complete();
        var saved = repo.save(ticket);

        autoCallQueue.callIfPartOfGroupComplete(ticket.getGroup());

        return ok(from(saved));
    }


    @Transactional
    public ResponseEntity<String> cancel() {
        var user = userService.getUserFromContextAndVerify();
        Ticket ticket = repo
                .findActiveByUserId(user.getId())
                .orElseThrow(throwActiveTicketNotFound());
        ticket.cancel();

        repo.save(ticket);
        return status(NO_CONTENT)
                .body("Ticket with id=%d has been cancelled!"
                        .formatted(ticket.getId()));
    }


    @Transactional
    public void callTickets(Collection<Ticket> tickets) {
        tickets.forEach(Ticket::call);
        repo.saveAll(tickets);
    }

    @Transactional
    public void completeTickets(Collection<Ticket> tickets) {
        tickets.forEach(Ticket::complete);
        repo.saveAll(tickets);
    }


    private static @NotNull Supplier<EntityNotFoundException> throwActiveTicketNotFound() {
        return () -> new EntityNotFoundException(
                "Active tickets not found!");
    }
}

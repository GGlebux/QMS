package most.qms.services;

import jakarta.persistence.EntityNotFoundException;
import most.qms.dtos.responses.CreatedTicketDto;
import most.qms.dtos.responses.OperationResultDto;
import most.qms.exceptions.EntityNotCreatedException;
import most.qms.interfaces.GroupCrud;
import most.qms.interfaces.PartCompletedQueue;
import most.qms.interfaces.TicketUpdater;
import most.qms.models.Group;
import most.qms.models.Ticket;
import most.qms.models.TicketStatus;
import most.qms.models.User;
import most.qms.repositories.TicketRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.EnumSet.of;
import static most.qms.dtos.responses.CreatedTicketDto.from;
import static most.qms.dtos.responses.OperationResultDto.OperationStatus.FAILURE;
import static most.qms.dtos.responses.OperationResultDto.OperationStatus.SUCCESS;
import static most.qms.models.TicketStatus.CALLED;
import static most.qms.models.TicketStatus.WAITING;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@Service
@Transactional(readOnly = true)
public class TicketService {
    private static final Logger log = getLogger(TicketService.class);
    private final TicketRepository repo;
    private final TicketUpdater updater;
    private final UserService userService;
    private final GroupCrud groupCrud;
    private final PartCompletedQueue partCompletedQueue;
    public static final EnumSet<TicketStatus> ACTIVE_TICKET_STATUSES;

    static {
        ACTIVE_TICKET_STATUSES = of(WAITING, CALLED);
    }

    @Autowired
    TicketService(TicketRepository repo, TicketUpdater updater,
                  UserService userService,
                  GroupCrud groupCrud,
                  PartCompletedQueue partCompletedQueue) {
        this.repo = repo;
        this.updater = updater;
        this.userService = userService;
        this.groupCrud = groupCrud;
        this.partCompletedQueue = partCompletedQueue;
    }

    public ResponseEntity<OperationResultDto> sendActiveTicket() {
        var user = userService.getUserFromContextAndVerify();
        var maybeTicket = user.getActiveTicket();
        if  (maybeTicket.isPresent()) {
            updater.updateOneTicket(maybeTicket.get());
            return ok(OperationResultDto.builder()
                    .status(SUCCESS)
                    .message("The ticket was successfully sent!")
                    .build());
        }
        return status(NOT_FOUND).body(OperationResultDto.builder()
                .status(FAILURE)
                .errors(Map.of("EntityNotFoundException", "No active tickets found!"))
                .build());
    }

    public List<CreatedTicketDto> findAll() {
        return repo.findAll().stream().map(CreatedTicketDto::from).toList();
    }


    @Transactional
    public ResponseEntity<CreatedTicketDto> create() {
        var user = userService.getUserFromContextAndVerify();
        throwIfHasActiveTickets(user);

        Group group = groupCrud.getLastAvailable();

        Ticket toSave = new Ticket(user, group);
        user.addTicket(toSave);
        group.addTicket(toSave);

        Ticket saved = repo.save(toSave);

        updater.updateOneTicket(saved);

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

        partCompletedQueue.callIfPartOfGroupComplete(ticket.getGroup());

        return ok(from(saved));
    }


    @Transactional
    public ResponseEntity<OperationResultDto> cancel() {
        var user = userService.getUserFromContextAndVerify();
        Ticket ticket = repo
                .findActiveByUserId(user.getId())
                .orElseThrow(throwActiveTicketNotFound());
        ticket.cancel();

        repo.save(ticket);

        updater.updateAllTickets();

        return status(NO_CONTENT)
                .body(OperationResultDto.builder()
                        .status(SUCCESS)
                        .message(("Ticket with id=%d has been cancelled!"
                                .formatted(ticket.getId())))
                        .build()
                );
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

    private static void throwIfHasActiveTickets(User user) {
        boolean hasActiveTickets = user
                .getTickets()
                .stream()
                .map(Ticket::getStatus)
                .anyMatch(ACTIVE_TICKET_STATUSES::contains);

        if (hasActiveTickets) {
            throw new EntityNotCreatedException(
                    "User with phoneNumber=%s already has active ticket!"
                            .formatted(user.getUsername()));
        }
    }


}

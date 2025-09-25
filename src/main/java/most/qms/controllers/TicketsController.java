package most.qms.controllers;

import most.qms.dtos.responses.TicketDto;
import most.qms.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/users/{userId}/tickets")
public class TicketsController {
    private final TicketService ticketService;

    @Autowired
    public TicketsController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketDto> create(@PathVariable Long userId) {
        return ok(ticketService.create(userId));
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@PathVariable Long userId) {
        ticketService.cancel(userId);
        return ok("Ticket has been cancelled");
    }
}

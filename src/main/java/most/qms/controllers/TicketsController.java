package most.qms.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import most.qms.dtos.responses.TicketDto;
import most.qms.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Билеты", description = "Возможности пользователя в очереди")
public class TicketsController {
    private final TicketService service;

    @Autowired
    public TicketsController(TicketService service) {
        this.service = service;
    }

    @RolesAllowed("ROLE_USER")
    @PostMapping
    @Operation(summary = "Занять свободное место в очереди",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TicketDto> create() {
        return service.create();
    }

    @RolesAllowed("ROLE_USER")
    @PatchMapping
    @Operation(summary = "Отметиться о прохождении границы",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TicketDto> markAsComplete() {
        return service.markAsComplete();
    }

    @RolesAllowed("ROLE_USER")
    @DeleteMapping
    @Operation(summary = "Выйти из очереди",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> cancel() {
        return service.cancel();
    }
}

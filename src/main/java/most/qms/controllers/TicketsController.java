package most.qms.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import most.qms.dtos.responses.CreatedTicketDto;
import most.qms.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RolesAllowed("ROLE_USER")
@Tag(name = "Билеты", description = "Возможности пользователя в очереди")
public class TicketsController {
    private final TicketService service;

    @Autowired
    public TicketsController(TicketService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Занять свободное место в очереди",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CreatedTicketDto> create() {
        return service.create();
    }

    @PatchMapping
    @Operation(summary = "Отметиться о прохождении границы",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CreatedTicketDto> markAsComplete() {
        return service.markAsComplete();
    }

    @DeleteMapping
    @Operation(summary = "Выйти из очереди",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> cancel() {
        return service.cancel();
    }
}

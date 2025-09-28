package most.qms.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import most.qms.models.Group;
import most.qms.services.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static java.util.Optional.empty;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/queue")
@RolesAllowed("ROLE_ADMIN")
public class QueueController {
    private final QueueService queueService;

    @Autowired
    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @PostMapping
    @Operation(summary = "Вызвать следующую группу",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> callNextGroup() {
        Optional<Group> called = queueService.callNextGroup(empty());
        return called
                .<ResponseEntity<?>>map(
                group -> ok("%s is calling!".
                        formatted(group.getName())))
                .orElseGet(() -> badRequest()
                        .body("Next group for calling not exists or empty!"));
    }
}

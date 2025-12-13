package most.qms.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import most.qms.services.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Optional.empty;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/queue")
// ToDo: Сделать разделение 1) Завершить группу 2) Вызвать следующую
public class QueueController {
    private final QueueService queueService;

    @Autowired
    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @PostMapping
    @RolesAllowed("ROLE_ADMIN")
    @Operation(summary = "Call the next group",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> callNextGroup() {
        return ok(queueService.callNextGroup(empty()));
    }
}

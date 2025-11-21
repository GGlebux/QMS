package most.qms.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.status;

@RestController
public class NotFoundController {
    @RequestMapping("/**")
    public ResponseEntity<String> fallback(HttpServletRequest request) {
        return status(NOT_FOUND)
                .body("Endpoint '%s' not found"
                        .formatted(request.getRequestURI()));
    }
}

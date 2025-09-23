package most.qms.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(VerificationException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public String handleVerificationException(VerificationException e) {
        return e.getMessage();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public String handleEntityNotFoundException(EntityNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(AuthException.class)
    @ResponseStatus(FORBIDDEN)
    @ResponseBody
    public String handleEntityNotFoundException(AuthException e) {
        return e.getMessage();
    }
}
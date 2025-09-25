package most.qms.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(VerificationException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public String handleVerificationException(VerificationException e) {
        return e.getMessage();
    }

    @ExceptionHandler(AuthException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public String handleAuthException(AuthException e) {
        return e.getMessage();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public String handleEntityNotFoundException(EntityNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(EntityNotCreatedException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public String handleEntityNotCreatedException(EntityNotCreatedException e) {
        return e.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return errors;
    }
}
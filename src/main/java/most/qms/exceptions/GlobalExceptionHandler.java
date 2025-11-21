package most.qms.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.of;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(VerificationException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleVerificationException(VerificationException e) {
        return convertExpToMap(e);
    }

    @ExceptionHandler(AuthException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleAuthException(AuthException e) {
        return convertExpToMap(e);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleEntityNotFoundException(EntityNotFoundException e) {
        return convertExpToMap(e);
    }

    @ExceptionHandler(EntityNotCreatedException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleEntityNotCreatedException(EntityNotCreatedException e) {
        return convertExpToMap(e);
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

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(FORBIDDEN)
    @ResponseBody
    public Map<String, Object> handleBadCredentialsException(BadCredentialsException e) {
        return convertExpToMap(e);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handleNotFound(NoHandlerFoundException ex) {
        return of(
                "error", "NotFound",
                "message", "Resource not found"
        );
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, Object> handleRuntimeException(Exception e) {
        return convertExpToMap(e);
    }

    private Map<String, Object> convertExpToMap(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", e.getClass().getSimpleName());
        response.put("message", e.getMessage());
        return response;
    }
}
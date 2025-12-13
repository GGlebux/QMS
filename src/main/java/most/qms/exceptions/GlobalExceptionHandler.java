package most.qms.exceptions;

import jakarta.persistence.EntityNotFoundException;
import most.qms.dtos.responses.OperationResultDto;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

import static most.qms.dtos.responses.OperationResultDto.OperationStatus.FAILURE;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(VerificationException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public OperationResultDto handleVerificationException(VerificationException e) {
        return convertToResDto(e);
    }

    @ExceptionHandler(AuthException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public OperationResultDto handleAuthException(AuthException e) {
        return convertToResDto(e);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public OperationResultDto handleEntityNotFoundException(EntityNotFoundException e) {
        return convertToResDto(e);
    }

    @ExceptionHandler(EntityNotCreatedException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public OperationResultDto handleEntityNotCreatedException(EntityNotCreatedException e) {
        return convertToResDto(e);
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
    public OperationResultDto handleBadCredentialsException(BadCredentialsException e) {
        return convertToResDto(e);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public OperationResultDto handleNotFound(NoHandlerFoundException ex) {
        return convertToResDto(ex);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ResponseBody
    public OperationResultDto handleRuntimeException(Exception e) {
        return convertToResDto(e);
    }

    private OperationResultDto convertToResDto(Exception e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", e.getClass().getSimpleName());
        errors.put("message", e.getMessage());
        
        return OperationResultDto.builder()
                .status(FAILURE)
                .errors(errors)
                .build();
    }
}
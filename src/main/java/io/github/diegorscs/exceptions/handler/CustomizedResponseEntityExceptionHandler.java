package io.github.diegorscs.exceptions.handler;

import io.github.diegorscs.exceptions.ExceptionResponse;
import io.github.diegorscs.exceptions.ResourceAlreadyExistsException;
import io.github.diegorscs.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handleAllException(Exception ex, WebRequest request) {
        return ResponseEntity.internalServerError().body(createExceptionReponse(ex, request));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createExceptionReponse(ex, request));
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public final ResponseEntity<ExceptionResponse> handleResourceAlreadyExistsException(
            ResourceAlreadyExistsException ex, WebRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(createExceptionReponse(ex, request));
    }


    private ExceptionResponse createExceptionReponse(Exception ex, WebRequest request) {
        return new ExceptionResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    }
}

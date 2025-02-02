package com.booking.api.shared;

import com.booking.api.shared.dto.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorMessage> handleGeneralException(
        final Exception ex
    ) {
        return ResponseEntity.badRequest().body(new ErrorMessage(ex.getMessage()));
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(
        final IllegalArgumentException ex
    ) {
        return ResponseEntity.badRequest().body(new ErrorMessage(ex.getMessage()));
    }

    @ExceptionHandler(value = IllegalStateException.class)
    public ResponseEntity<ErrorMessage> handleIllegalStateException(
            final IllegalStateException ex
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMessage(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorMessage>> handleValidationExceptions(
        final MethodArgumentNotValidException ex
    ) {
        final var errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> new ErrorMessage(fieldError.getDefaultMessage()))
                .collect(Collectors.toList());

        return ResponseEntity.badRequest().body(errors);
    }

}

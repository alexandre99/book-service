package com.booking.api.shared;

import com.booking.api.shared.dto.ErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
        return ResponseEntity.badRequest().body(new ErrorMessage(ex.getMessage()));
    }

}

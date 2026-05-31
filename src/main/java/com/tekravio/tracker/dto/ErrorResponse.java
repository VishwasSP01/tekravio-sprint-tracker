package com.tekravio.tracker.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        boolean success,
        String message,
        List<FieldError> errors,
        LocalDateTime timestamp) {

    public record FieldError(String field, String message) {
    }

    public static ErrorResponse of(String message) {
        return new ErrorResponse(false, message, List.of(), LocalDateTime.now());
    }
}

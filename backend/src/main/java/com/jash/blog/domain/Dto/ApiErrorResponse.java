package com.jash.blog.domain.Dto;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        int status,
        String message,
        List<FieldError> errors,
        Instant timestamp
) {
    // Compact constructor - runs before the auto-generated one
    // We use this to set a default timestamp if none is provided
    public ApiErrorResponse(int status, String message, List<FieldError> errors) {
        this(status, message, errors, Instant.now());
    }

    // Convenience constructor for simple errors with no field errors
    public ApiErrorResponse(int status, String message) {
        this(status, message, List.of(), Instant.now());
    }

    public record FieldError(
            String field,
            String message
    ) {}
}
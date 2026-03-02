package com.jash.blog.domain.Dto;

public record AuthResponse(
        String token,
        long expiresIn
) {
}

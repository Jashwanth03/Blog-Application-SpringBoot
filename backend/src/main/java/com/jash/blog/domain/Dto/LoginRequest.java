package com.jash.blog.domain.Dto;

public record LoginRequest(
        String email,
        String password
) {
}

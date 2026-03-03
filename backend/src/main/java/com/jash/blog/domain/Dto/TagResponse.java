package com.jash.blog.domain.Dto;

import java.util.UUID;

public record TagResponse(
        UUID id,
        String name,
        Integer postCount
) {
}

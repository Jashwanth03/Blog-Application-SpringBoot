package com.jash.blog.domain.Dto;



import java.util.UUID;


public record CategoryDto(
         UUID id,
         String name,
         long postCount
) {
}

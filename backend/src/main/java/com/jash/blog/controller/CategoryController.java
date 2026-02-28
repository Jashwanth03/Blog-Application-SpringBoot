package com.jash.blog.controller;


import com.jash.blog.domain.Dto.CategoryDto;
import com.jash.blog.domain.Dto.CreateCategoryRequest;
import com.jash.blog.domain.entities.Category;
import com.jash.blog.mappers.CategoryMapper;
import com.jash.blog.service.Impl.CategoryServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/categories")
@RequiredArgsConstructor //Creates Constructor for each final fields
public class CategoryController {

    private final CategoryServiceImpl categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> listCategories() {
        List<CategoryDto> categories = categoryService.listCategories()
                .stream().map(categoryMapper::mapToDto)
                .toList();

        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        Category categoryToCreate = categoryMapper.maptoEntity(createCategoryRequest);

        Category savedCategory = categoryService.createCategory(categoryToCreate);

        return new ResponseEntity<>(
                categoryMapper.mapToDto(savedCategory), HttpStatus.CREATED
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

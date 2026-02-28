package com.jash.blog.service.Impl;


import com.jash.blog.domain.entities.Category;
import com.jash.blog.repository.CategoryRepository;
import com.jash.blog.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;


@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> listCategories() {

        return categoryRepository.findAllWithPostCount();
    }

    @Override
    public Category createCategory(Category category) {

        String categoryName = category.getName();
        if(categoryRepository.existsByNameIgnoreCase(categoryName)){
            throw new IllegalArgumentException("Category with name " + categoryName + " already exists");
        }
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(UUID id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category with id " + id + " not found"));
        if (!category.getPosts().isEmpty()) {
            throw new IllegalArgumentException("Category with id " + id + " already has posts");
        }
        categoryRepository.delete(category);
    }


}

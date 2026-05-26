package com.arthurpv15.apimanagement.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.arthurpv15.apimanagement.entity.Category;
import com.arthurpv15.apimanagement.exception.ResourceNotFoundException;
import com.arthurpv15.apimanagement.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<Category> searchAll() {
        return repository.findAll();
    }

    public Category findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }
}

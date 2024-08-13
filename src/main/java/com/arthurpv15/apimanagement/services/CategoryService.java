package com.arthurpv15.apimanagement.services;

import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arthurpv15.apimanagement.repository.CategoryRepository;
import com.arthurpv15.apimanagement.entity.Category;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repository;

    public List<Category> searchAll(){
        return repository.findAll();
    }

    public Category findById(Long id){
        Optional<Category> category = repository.findById(id);
        return category.get();
    }
}
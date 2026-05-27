package com.arthurpv15.apimanagement.config;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.arthurpv15.apimanagement.entity.Category;
import com.arthurpv15.apimanagement.repository.CategoryRepository;

@Component
public class CategoryInitializer implements ApplicationRunner {

    private final CategoryRepository categoryRepository;

    public CategoryInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (categoryRepository.count() == 0) {
            categoryRepository.saveAll(List.of(
                    new Category("Shopping"),
                    new Category("Food"),
                    new Category("Job"),
                    new Category("Streamings")
            ));
        }
    }
}

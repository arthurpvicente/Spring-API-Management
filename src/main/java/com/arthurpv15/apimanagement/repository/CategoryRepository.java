package com.arthurpv15.apimanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.arthurpv15.apimanagement.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
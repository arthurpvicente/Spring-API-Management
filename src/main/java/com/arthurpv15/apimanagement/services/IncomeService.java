package com.arthurpv15.apimanagement.services;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.arthurpv15.apimanagement.dto.IncomeRequest;
import com.arthurpv15.apimanagement.entity.Category;
import com.arthurpv15.apimanagement.entity.Income;
import com.arthurpv15.apimanagement.entity.User;
import com.arthurpv15.apimanagement.enums.IncomeStatus;
import com.arthurpv15.apimanagement.exception.ForbiddenAccessException;
import com.arthurpv15.apimanagement.exception.ResourceNotFoundException;
import com.arthurpv15.apimanagement.repository.CategoryRepository;
import com.arthurpv15.apimanagement.repository.IncomeRepository;
import com.arthurpv15.apimanagement.repository.UserRepository;

@Service
public class IncomeService {

    private final IncomeRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public IncomeService(IncomeRepository repository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Income> searchAllForUser(String email) {
        User user = findUserByEmail(email);
        return repository.findByUser_Id(user.getId());
    }

    public Income findById(Long id, String email) {
        Income income = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income", id));
        verifyOwnership(income, email);
        return income;
    }

    public Income insert(IncomeRequest request, String email) {
        User user = findUserByEmail(email);
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));

        Income income = new Income(
                request.title(),
                request.value(),
                Instant.now(),
                IncomeStatus.valueOfCode(request.status()),
                user,
                category
        );
        return repository.save(income);
    }

    public Income update(Long id, IncomeRequest request, String email) {
        Income income = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income", id));
        verifyOwnership(income, email);

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));

        income.setTitle(request.title());
        income.setValue(request.value());
        income.setStatus(IncomeStatus.valueOfCode(request.status()));
        income.setCategoryIncome(category);

        return repository.save(income);
    }

    public void delete(Long id, String email) {
        Income income = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income", id));
        verifyOwnership(income, email);
        repository.deleteById(id);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void verifyOwnership(Income income, String email) {
        User user = findUserByEmail(email);
        if (!income.getUser().getId().equals(user.getId())) {
            throw new ForbiddenAccessException();
        }
    }
}

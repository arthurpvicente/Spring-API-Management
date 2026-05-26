package com.arthurpv15.apimanagement.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arthurpv15.apimanagement.dto.IncomeRequest;
import com.arthurpv15.apimanagement.entity.Income;
import com.arthurpv15.apimanagement.services.IncomeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/incomes")
@Tag(name = "Incomes", description = "Income tracking endpoints")
public class IncomeController {

    private final IncomeService service;

    public IncomeController(IncomeService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Retrieve all incomes for the authenticated user")
    public ResponseEntity<List<Income>> findAll(Principal principal) {
        List<Income> list = service.searchAllForUser(principal.getName());
        return ResponseEntity.ok(list);
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Retrieve an income by ID")
    public ResponseEntity<Income> findById(@PathVariable Long id, Principal principal) {
        Income income = service.findById(id, principal.getName());
        return ResponseEntity.ok(income);
    }

    @PostMapping
    @Operation(summary = "Create a new income")
    public ResponseEntity<Income> insert(@Valid @RequestBody IncomeRequest request, Principal principal) {
        Income income = service.insert(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(income);
    }

    @PutMapping(value = "/{id}")
    @Operation(summary = "Update an income by ID")
    public ResponseEntity<Income> update(@PathVariable Long id, @Valid @RequestBody IncomeRequest request, Principal principal) {
        Income income = service.update(id, request, principal.getName());
        return ResponseEntity.ok(income);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete an income by ID")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        service.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}

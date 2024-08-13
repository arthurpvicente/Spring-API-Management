package com.arthurpv15.apimanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arthurpv15.apimanagement.entity.Income;
import com.arthurpv15.apimanagement.services.IncomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping(value = "/incomes")
public class IncomeController {
    @Autowired
    IncomeService service;

    @GetMapping
    public ResponseEntity<List<Income>> findAll() {
        List<Income> list = service.searchAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Income> findById(@PathVariable Long id) {
        Income income = service.findById(id);
        return ResponseEntity.ok().body(income);
    }
}
package com.arthurpv15.apimanagement.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arthurpv15.apimanagement.entity.Income;
import com.arthurpv15.apimanagement.repository.IncomeRepository;

@Service
public class IncomeService {

    @Autowired
    private IncomeRepository repository;

    public List<Income> searchAll(){
        return repository.findAll();
    }

    public Income findById(Long id){
        Optional<Income> income = repository.findById(id);
        return income.get();
    }

}

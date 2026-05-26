package com.arthurpv15.apimanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arthurpv15.apimanagement.entity.Income;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findByUser_Id(Long userId);
}
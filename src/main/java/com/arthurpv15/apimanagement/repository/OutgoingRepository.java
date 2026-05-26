package com.arthurpv15.apimanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arthurpv15.apimanagement.entity.Outgoing;

public interface OutgoingRepository extends JpaRepository<Outgoing, Long> {

    List<Outgoing> findByUser_Id(Long userId);
}
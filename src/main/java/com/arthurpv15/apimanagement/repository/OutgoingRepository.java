package com.arthurpv15.apimanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arthurpv15.apimanagement.entity.Outgoing;

public interface OutgoingRepository extends JpaRepository<Outgoing, Long> {

}
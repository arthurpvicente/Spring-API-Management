package com.arthurpv15.apimanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.arthurpv15.apimanagement.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
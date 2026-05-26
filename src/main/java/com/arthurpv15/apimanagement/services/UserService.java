package com.arthurpv15.apimanagement.services;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.arthurpv15.apimanagement.dto.UserRequest;
import com.arthurpv15.apimanagement.dto.UserResponse;
import com.arthurpv15.apimanagement.entity.User;
import com.arthurpv15.apimanagement.exception.ForbiddenAccessException;
import com.arthurpv15.apimanagement.exception.ResourceNotFoundException;
import com.arthurpv15.apimanagement.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList()
        );
    }

    public List<UserResponse> searchAll() {
        return repository.findAll().stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    public UserResponse findById(Long id, String email) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        verifyOwnership(user, email);
        return UserResponse.fromEntity(user);
    }

    public UserResponse insert(UserRequest request) {
        User user = new User(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password())
        );
        user = repository.save(user);
        return UserResponse.fromEntity(user);
    }

    public void delete(Long id, String email) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        verifyOwnership(user, email);
        repository.deleteById(id);
    }

    public UserResponse update(Long id, UserRequest request, String email) {
        User currentUser = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        verifyOwnership(currentUser, email);

        currentUser.setName(request.name());
        currentUser.setEmail(request.email());
        if (request.password() != null && !request.password().isBlank()) {
            currentUser.setPassword(passwordEncoder.encode(request.password()));
        }

        currentUser = repository.save(currentUser);
        return UserResponse.fromEntity(currentUser);
    }

    private void verifyOwnership(User user, String email) {
        if (!user.getEmail().equals(email)) {
            throw new ForbiddenAccessException();
        }
    }
}

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

import com.arthurpv15.apimanagement.dto.UserRequest;
import com.arthurpv15.apimanagement.dto.UserResponse;
import com.arthurpv15.apimanagement.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/users")
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Retrieve all users")
    public ResponseEntity<List<UserResponse>> findAll() {
        List<UserResponse> list = service.searchAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Retrieve a user by ID")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id, Principal principal) {
        UserResponse user = service.findById(id, principal.getName());
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<UserResponse> insert(@Valid @RequestBody UserRequest request) {
        UserResponse user = service.insert(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete a user by ID")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        service.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}")
    @Operation(summary = "Update a user by ID")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserRequest request, Principal principal) {
        UserResponse user = service.update(id, request, principal.getName());
        return ResponseEntity.ok(user);
    }
}

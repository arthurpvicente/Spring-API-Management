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

import com.arthurpv15.apimanagement.dto.OutgoingRequest;
import com.arthurpv15.apimanagement.entity.Outgoing;
import com.arthurpv15.apimanagement.services.OutgoingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/outgoings")
@Tag(name = "Outgoings", description = "Outgoing expense tracking endpoints")
public class OutgoingController {

    private final OutgoingService service;

    public OutgoingController(OutgoingService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Retrieve all outgoings for the authenticated user")
    public ResponseEntity<List<Outgoing>> findAll(Principal principal) {
        List<Outgoing> list = service.searchAllForUser(principal.getName());
        return ResponseEntity.ok(list);
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Retrieve an outgoing by ID")
    public ResponseEntity<Outgoing> findById(@PathVariable Long id, Principal principal) {
        Outgoing outgoing = service.findById(id, principal.getName());
        return ResponseEntity.ok(outgoing);
    }

    @PostMapping
    @Operation(summary = "Create a new outgoing")
    public ResponseEntity<Outgoing> insert(@Valid @RequestBody OutgoingRequest request, Principal principal) {
        Outgoing outgoing = service.insert(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(outgoing);
    }

    @PutMapping(value = "/{id}")
    @Operation(summary = "Update an outgoing by ID")
    public ResponseEntity<Outgoing> update(@PathVariable Long id, @Valid @RequestBody OutgoingRequest request, Principal principal) {
        Outgoing outgoing = service.update(id, request, principal.getName());
        return ResponseEntity.ok(outgoing);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete an outgoing by ID")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        service.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}

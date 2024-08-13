package com.arthurpv15.apimanagement.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arthurpv15.apimanagement.entity.Outgoing;
import com.arthurpv15.apimanagement.services.OutgoingService;

@RestController
@RequestMapping(value = "/outgoings")
public class OutgoingController {
    @Autowired
    OutgoingService service;

    @GetMapping
    public ResponseEntity<List<Outgoing>> findAll(){
        List<Outgoing> list = service.searchAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Outgoing> findAll(@PathVariable Long id){
        Outgoing outgoing = service.findById(id);
        return ResponseEntity.ok().body(outgoing);
    }

}
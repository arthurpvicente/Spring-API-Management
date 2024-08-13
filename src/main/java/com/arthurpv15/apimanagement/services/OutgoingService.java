package com.arthurpv15.apimanagement.services;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arthurpv15.apimanagement.entity.Outgoing;
import com.arthurpv15.apimanagement.repository.OutgoingRepository;

@Service
public class OutgoingService {
    @Autowired
    private OutgoingRepository repository;

    public List<Outgoing> searchAll(){
        return repository.findAll();
    }
    
    public Outgoing findById(Long id){
        Optional<Outgoing> outgoing = repository.findById(id);
        return outgoing.get();
    }

}
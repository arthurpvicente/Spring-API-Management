package com.arthurpv15.apimanagement.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.arthurpv15.apimanagement.entity.User;
import com.arthurpv15.apimanagement.services.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping(value = "/users")
public class UserController {
    @Autowired
    UserService service;
    @GetMapping
    public ResponseEntity<List<User>> findAll(){
        List<User> list = service.searchAll();
        return ResponseEntity.ok().body(list);

    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<User> findAll(@PathVariable Long id){
        User user = service.findById(id);
        return ResponseEntity.ok().body(user);
    }

    @PostMapping
    public ResponseEntity<User> insert(@RequestBody User newUser) {
        newUser = service.insert(newUser);
        URI uri = ServletUriComponentsBuilder. //serve to build a new URI
                fromCurrentRequest().
                path("/{id}"). //path to the new resource
                buildAndExpand(newUser.getId()). //expand the path with the new resource id
                toUri(); //Convert to the result of previous operations for the format of URI 
        
        return ResponseEntity.created(uri).body(newUser);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id); //Delete in databases calling the method created inside of UserService
        return ResponseEntity.noContent().build(); // Requisition of deletion in the user
    }
    
    @PutMapping(value = "/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User userUpdate) {
        userUpdate = service.update(id, userUpdate); //Update in databases calling the method created inside of UserService
        return ResponseEntity.ok().body(userUpdate);
    }
}
package com.arthurpv15.apimanagement.services;

import com.arthurpv15.apimanagement.entity.User;
import com.arthurpv15.apimanagement.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public List<User> searchAll(){
        return repository.findAll();
    }

    public User findById(Long id){
        Optional<User> user = repository.findById(id);
        return user.get();
    }
    
    public User insert (User newUser){
        return repository.save(newUser);
    }

    public void delete (Long id){
        repository.deleteById(id);
    }

    public User update(Long id, User userUpdate){
        User currentUser = repository.getReferenceById(id);
        updateInformation(currentUser, userUpdate);
        return repository.save(currentUser);
    }

    public void updateInformation(User currentUser, User userUpdate){
        currentUser.setName(userUpdate.getName());
        currentUser.setEmail(userUpdate.getEmail());
        currentUser.setPassword(userUpdate.getPassword());
    }
}

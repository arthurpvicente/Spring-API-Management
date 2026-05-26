package com.arthurpv15.apimanagement.services;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.arthurpv15.apimanagement.dto.OutgoingRequest;
import com.arthurpv15.apimanagement.entity.Category;
import com.arthurpv15.apimanagement.entity.Outgoing;
import com.arthurpv15.apimanagement.entity.User;
import com.arthurpv15.apimanagement.enums.OutgoingStatus;
import com.arthurpv15.apimanagement.exception.ForbiddenAccessException;
import com.arthurpv15.apimanagement.exception.ResourceNotFoundException;
import com.arthurpv15.apimanagement.repository.CategoryRepository;
import com.arthurpv15.apimanagement.repository.OutgoingRepository;
import com.arthurpv15.apimanagement.repository.UserRepository;

@Service
public class OutgoingService {

    private final OutgoingRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public OutgoingService(OutgoingRepository repository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Outgoing> searchAllForUser(String email) {
        User user = findUserByEmail(email);
        return repository.findByUser_Id(user.getId());
    }

    public Outgoing findById(Long id, String email) {
        Outgoing outgoing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Outgoing", id));
        verifyOwnership(outgoing, email);
        return outgoing;
    }

    public Outgoing insert(OutgoingRequest request, String email) {
        User user = findUserByEmail(email);
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));

        Outgoing outgoing = new Outgoing(
                request.title(),
                request.value(),
                Instant.now(),
                OutgoingStatus.valueOfCode(request.status()),
                user,
                category
        );
        return repository.save(outgoing);
    }

    public Outgoing update(Long id, OutgoingRequest request, String email) {
        Outgoing outgoing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Outgoing", id));
        verifyOwnership(outgoing, email);

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));

        outgoing.setTitle(request.title());
        outgoing.setValue(request.value());
        outgoing.setStatus(OutgoingStatus.valueOfCode(request.status()));
        outgoing.setCategoryOutgoing(category);

        return repository.save(outgoing);
    }

    public void delete(Long id, String email) {
        Outgoing outgoing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Outgoing", id));
        verifyOwnership(outgoing, email);
        repository.deleteById(id);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void verifyOwnership(Outgoing outgoing, String email) {
        User user = findUserByEmail(email);
        if (!outgoing.getUser().getId().equals(user.getId())) {
            throw new ForbiddenAccessException();
        }
    }
}

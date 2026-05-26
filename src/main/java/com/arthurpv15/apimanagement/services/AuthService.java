package com.arthurpv15.apimanagement.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.arthurpv15.apimanagement.dto.LoginRequest;
import com.arthurpv15.apimanagement.dto.LoginResponse;
import com.arthurpv15.apimanagement.dto.UserRequest;
import com.arthurpv15.apimanagement.dto.UserResponse;
import com.arthurpv15.apimanagement.entity.User;
import com.arthurpv15.apimanagement.repository.UserRepository;
import com.arthurpv15.apimanagement.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponse register(UserRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password())
        );

        user = userRepository.save(user);
        return UserResponse.fromEntity(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new LoginResponse(token, jwtService.getExpiration());
    }
}

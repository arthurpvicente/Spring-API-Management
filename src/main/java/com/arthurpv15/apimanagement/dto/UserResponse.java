package com.arthurpv15.apimanagement.dto;

import com.arthurpv15.apimanagement.entity.User;

public record UserResponse(Long id, String name, String email) {

    public static UserResponse fromEntity(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}

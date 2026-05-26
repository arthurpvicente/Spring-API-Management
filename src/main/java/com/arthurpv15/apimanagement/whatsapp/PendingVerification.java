package com.arthurpv15.apimanagement.whatsapp;

import java.time.Instant;

public class PendingVerification {

    private final String email;
    private final String code;
    private final Instant createdAt;

    public PendingVerification(String email, String code, Instant createdAt) {
        this.email = email;
        this.code = code;
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public String getCode() {
        return code;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

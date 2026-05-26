package com.arthurpv15.apimanagement.whatsapp;

import java.time.Instant;

public class PendingEntry {

    private final String title;
    private final Double value;
    private final boolean income;
    private final Instant createdAt;

    public PendingEntry(String title, Double value, boolean income) {
        this.title = title;
        this.value = value;
        this.income = income;
        this.createdAt = Instant.now();
    }

    public String getTitle() {
        return title;
    }

    public Double getValue() {
        return value;
    }

    public boolean isIncome() {
        return income;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

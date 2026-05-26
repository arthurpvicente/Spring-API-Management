package com.arthurpv15.apimanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IncomeRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotNull(message = "Value is required")
        @Positive
        Double value,

        @NotNull(message = "Status is required")
        Integer status,

        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Category ID is required")
        Long categoryId
) {}

package com.booking.api.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public record PropertyRequestDTO(
        @NotNull
        UUID ownerId,
        @NotBlank
        String name,
        @NotEmpty
        @NotNull
        Set<@NotBlank String> amenities) {
}

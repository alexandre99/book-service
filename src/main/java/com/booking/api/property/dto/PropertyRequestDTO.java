package com.booking.api.property.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.Set;

public record PropertyRequestDTO(
        @NotBlank
        String name,
        @NotBlank
        String hostName,
        @NotBlank
        String address,
        @NotEmpty
        @NotNull
        Set<@NotBlank String> amenities,
        @NotNull
        LocalTime checkInTime,
        @NotNull
        LocalTime checkOutTime,
        @DecimalMin(value = "1.00")
        Float dailyRate) {
}

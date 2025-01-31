package com.booking.api.property.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.Set;

public record PropertyRequestDTO(
        @NotBlank(message = "name must no be empty or null")
        String name,
        @NotBlank(message = "hostName must no be empty or null")
        String hostName,
        @NotBlank(message = "address must no be empty or null")
        String address,
        @NotEmpty(message = "amenities must no be empty")
        @NotNull(message = "amenities must no be null")
        Set<@NotBlank String> amenities,
        @NotNull(message = "checkInTime must not be null")
        LocalTime checkInTime,
        @NotNull(message = "checkOutTime must not be null")
        LocalTime checkOutTime,
        @DecimalMin(value = "1.00", message = "dailyRate must be equal to or greater than 1.00")
        Float dailyRate) {
}

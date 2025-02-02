package com.booking.api.property.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record BlockPropertyRequest(
    @NotNull(message = "propertyId must not be null")
    UUID propertyId,
    @NotNull(message = "startDate must not be null")
    @FutureOrPresent(message = "startDate must be in the present or future")
    LocalDate startDate,
    @NotNull(message = "endDate must not be null")
    @Future(message = "endDate must be in the future")
    LocalDate endDate
) {
}

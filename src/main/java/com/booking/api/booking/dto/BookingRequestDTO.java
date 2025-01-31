package com.booking.api.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record BookingRequestDTO(
    @NotNull
    UUID propertyId,
    @NotNull
    @FutureOrPresent
    LocalDate startDate,
    @NotNull
    @Future
    LocalDate endDate,
    @NotNull
    GuestDetailsDTO guestDetails
) {
}

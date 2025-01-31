package com.booking.api.booking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record GuestDetailsDTO(
    @NotBlank
    String fullName,
    @NotBlank
    String email,
    @NotBlank
    String phone,
    @Min(value = 1)
    int numberOfAdults,
    int numberOfChildren,
    int numberOfInfants,
    String specialRequests
) {
}

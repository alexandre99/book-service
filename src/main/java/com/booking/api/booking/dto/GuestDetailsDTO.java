package com.booking.api.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record GuestDetailsDTO(
    @NotBlank(message = "fullName must not be empty or null")
    String fullName,
    @NotBlank(message = "fullName email not be empty or null")
    @Email(message = "email must be a valid email")
    String email,
    @NotBlank(message = "phone email not be empty or null")
    String phone,
    @Min(value = 1, message = "numberOfAdults must be equal to or greater than 1")
    int numberOfAdults,
    int numberOfChildren,
    int numberOfInfants,
    String specialRequests
) {
}

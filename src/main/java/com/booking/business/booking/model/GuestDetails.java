package com.booking.business.booking.model;

public record GuestDetails(
    String fullName,
    String email,
    String phone,
    int numberOfAdults,
    int numberOfChildren,
    int numberOfInfants,
    String specialRequests
) {
}

package com.booking.dataprovider.booking.model;

import jakarta.persistence.Embeddable;

@Embeddable
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

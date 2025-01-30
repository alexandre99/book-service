package com.booking.dataprovider.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record GuestDetails(
    @Column(nullable = false)
    String fullName,
    @Column(nullable = false)
    String email,
    @Column(nullable = false)
    String phone,
    int numberOfAdults,
    int numberOfChildren,
    int numberOfInfants,
    String specialRequests
) {

}

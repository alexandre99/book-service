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

    public static GuestDetails from(final com.booking.business.booking.model.GuestDetails guestDetails) {
        return new GuestDetails(
            guestDetails.fullName(),
            guestDetails.email(),
            guestDetails.phone(),
            guestDetails.numberOfAdults(),
            guestDetails.numberOfChildren(),
            guestDetails.numberOfInfants(),
            guestDetails.specialRequests()
        );
    }

}

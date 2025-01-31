package com.booking.business.booking.model;

import java.time.LocalDate;
import java.util.UUID;

public record Booking(
     UUID id,
     UUID propertyId,
     LocalDate startDate,
     LocalDate endDate,
     GuestDetails guestDetails
) {
    public boolean isStartDateEqualsOrAfterEndDate() {
        return startDate.isEqual(endDate) || startDate.isAfter(endDate);
    }
}

package com.booking.business.booking.model;

import com.booking.business.property.model.BookedPropertyDetailsView;

import java.time.LocalDate;
import java.util.UUID;

public record BookingView(
    UUID id,
    LocalDate startDate,
    LocalDate endDate,
    State state,
    GuestDetails guestDetails,
    BookedPropertyDetailsView propertyDetails
) {
}

package com.booking.business.booking.projection;

import java.time.LocalDate;
import java.util.UUID;

public record BookingWithPropertyAndDates(
    UUID propertyId,
    LocalDate startDate,
    LocalDate endDate) {
}

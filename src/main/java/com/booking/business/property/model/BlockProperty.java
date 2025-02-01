package com.booking.business.property.model;

import java.time.LocalDate;
import java.util.UUID;

public record BlockProperty(
    UUID id,
    UUID propertyId,
    LocalDate startDate,
    LocalDate endDate
) {
    public boolean isStartDateEqualsOrAfterEndDate() {
        return startDate.isEqual(endDate) || startDate.isAfter(endDate);
    }
}

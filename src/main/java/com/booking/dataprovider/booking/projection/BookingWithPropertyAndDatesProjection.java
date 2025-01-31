package com.booking.dataprovider.booking.projection;

import java.time.LocalDate;
import java.util.UUID;

public interface BookingWithPropertyAndDatesProjection {
    UUID getPropertyId();
    LocalDate getStartDate();
    LocalDate getEndDate();
}

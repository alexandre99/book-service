package com.booking.business.property.model;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record PropertyView(
        UUID id,
        String name,
        String hostName,
        Set<String> amenities,
        Instant createdAt,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        Float dailyRate) {
}

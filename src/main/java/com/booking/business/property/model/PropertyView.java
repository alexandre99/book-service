package com.booking.business.property.model;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record PropertyView(
        UUID id,
        UUID ownerId,
        String name,
        Set<String> amenities,
        Instant createdAt) {
}

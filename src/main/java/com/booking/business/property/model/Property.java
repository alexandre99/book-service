package com.booking.business.property.model;

import java.util.Set;
import java.util.UUID;

public record Property(
        UUID id,
        UUID ownerId,
        String name,
        Set<String> amenities
) {
}

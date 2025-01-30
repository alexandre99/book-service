
package com.booking.business.property.model;

import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record Property(
        UUID id,
        String name,
        String hostName,
        String address,
        Set<String> amenities,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        Float dailyRate
) {
}

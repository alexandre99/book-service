package com.booking.business.property.model;

import java.util.UUID;

public record PropertySampleView(
        UUID id,
        String name,
        String address,
        Float dailyRate
) {
}

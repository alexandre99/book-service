package com.booking.business.property.model;

import java.util.UUID;

public record PropertySummaryView(
        UUID id,
        String name,
        String address,
        Float dailyRate
) {
}

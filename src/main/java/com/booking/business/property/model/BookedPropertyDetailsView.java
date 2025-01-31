package com.booking.business.property.model;

import java.util.UUID;

public record BookedPropertyDetailsView(
    UUID id,
    String name
) {
}

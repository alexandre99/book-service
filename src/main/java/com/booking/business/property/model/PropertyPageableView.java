package com.booking.business.property.model;

import java.util.Set;

public record PropertyPageableView(
        Set<PropertySampleView> content,
        long totalItems,
        int totalPages,
        int currentPage) {

    public static PropertyPageableView empty() {
        return new PropertyPageableView(Set.of(), 0, 0 ,0);
    }
}

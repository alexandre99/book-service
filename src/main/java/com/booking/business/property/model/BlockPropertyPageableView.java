package com.booking.business.property.model;

import java.util.Set;

public record BlockPropertyPageableView(
        Set<BlockProperty> content,
        long totalItems,
        int totalPages,
        int currentPage
) {
    public static BlockPropertyPageableView empty() {
        return new BlockPropertyPageableView(Set.of(), 0, 0 ,0);
    }
}

package com.booking.business.shared.repository;

import java.time.LocalDate;
import java.util.UUID;

public interface OverlapRepository {

    boolean hasOverlap(final UUID propertyId,
                       final LocalDate startDate,
                       final LocalDate endDate);

}

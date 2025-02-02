package com.booking.business.shared.service;

import com.booking.business.shared.repository.OverlapRepository;

import java.time.LocalDate;
import java.util.UUID;

public abstract class OverlapValidationService {

    private final OverlapRepository repository;

    protected OverlapValidationService(final OverlapRepository repository) {
        this.repository = repository;
    }

    public void validateOverLap(final UUID propertyId,
                                final LocalDate startDate,
                                final LocalDate endDate,
                                final String validationMessage) {
        final var hasOverLap = this.repository.hasOverlap(propertyId, startDate, endDate);
        if (hasOverLap) {
            throw new IllegalStateException(validationMessage);
        }
    }

}

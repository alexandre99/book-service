package com.booking.business.property.service;

import com.booking.business.property.model.BlockProperty;

import java.time.LocalDate;
import java.util.UUID;

public interface BlockPropertyService {

    UUID save(BlockProperty blockProperty);

    void deleteById(UUID id);

    void validateOverLap(
            final UUID propertyId,
            final LocalDate startDate,
            final LocalDate endDate,
            final String validationMessage);

}

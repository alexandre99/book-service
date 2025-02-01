package com.booking.business.property.service;

import com.booking.business.property.model.BlockProperty;

import java.time.LocalDate;
import java.util.UUID;

public interface BlockPropertyRepository {

    UUID save(BlockProperty blockProperty);

    void deleteById(UUID id);

    boolean hasOverLap(UUID propertyId, LocalDate startDate, LocalDate endDate);

    boolean existsById(UUID id);
}

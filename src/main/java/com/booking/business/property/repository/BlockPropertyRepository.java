package com.booking.business.property.repository;

import com.booking.business.property.model.BlockProperty;
import com.booking.business.property.model.BlockPropertyPageableView;

import java.util.Optional;
import java.util.UUID;

public interface BlockPropertyRepository {

    UUID save(BlockProperty blockProperty);

    Optional<BlockProperty> findById(UUID id);

    BlockPropertyPageableView findAll(final int page, final int limit);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}

package com.booking.dataprovider.property.repository;

import com.booking.business.property.model.BlockProperty;
import com.booking.business.property.service.BlockPropertyRepository;
import com.booking.dataprovider.property.entity.BlockPropertyJpaEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class BlockPropertyRepositoryImpl implements BlockPropertyRepository {

    private final BlockPropertyJpaEntityRepository delegate;

    public BlockPropertyRepositoryImpl(final BlockPropertyJpaEntityRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public UUID save(final BlockProperty blockProperty) {
        final var entity = BlockPropertyJpaEntity.from(blockProperty);
        return delegate.save(entity).getId();
    }

    @Transactional
    @Override
    public void deleteById(final UUID id) {
        this.delegate.deleteById(id);
    }

    @Override
    public boolean existsById(final UUID id) {
        return this.delegate.existsById(id);
    }

}

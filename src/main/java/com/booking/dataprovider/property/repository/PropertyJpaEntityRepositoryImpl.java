package com.booking.dataprovider.property.repository;

import com.booking.business.property.model.Property;
import com.booking.business.property.model.PropertyPageableView;
import com.booking.business.property.model.PropertyView;
import com.booking.business.property.repository.PropertyRepository;
import com.booking.dataprovider.property.entity.PropertyJpaEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class PropertyJpaEntityRepositoryImpl implements PropertyRepository {

    private final PropertyJpaEntityRepository delegate;

    public PropertyJpaEntityRepositoryImpl(final PropertyJpaEntityRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public UUID save(final Property property) {
        final var entity = PropertyJpaEntity.from(property);
        return this.delegate.save(entity).getId();
    }

    @Override
    public PropertyPageableView findAll(final int page, final int limit) {
        final var results = delegate.findByEnableTrue(PageRequest.of(page - 1, limit));
        if (results.isEmpty()) {
            return PropertyPageableView.empty();
        }
        final var content = results.stream().map(
                entity -> new PropertyView(
                        entity.getId(),
                        entity.getOwnerId(),
                        entity.getName(),
                        entity.getAmenities(),
                        entity.getCreatedAt()
                )).collect(Collectors.toSet());

        return new PropertyPageableView(
            content,
            results.getTotalElements(),
            results.getTotalPages(),
            results.getNumber() + 1
        );
    }

    @Override
    public Optional<PropertyView> findById(final UUID id) {
        return delegate.findByIdAndEnableTrue(id)
                .map(entity -> new PropertyView(
                        entity.getId(),
                        entity.getOwnerId(),
                        entity.getName(),
                        entity.getAmenities(),
                        entity.getCreatedAt()
                ))
                .or(Optional::empty);
    }

    @Transactional
    @Override
    public void disable(final UUID id) {
        this.delegate.disable(id);
    }
}

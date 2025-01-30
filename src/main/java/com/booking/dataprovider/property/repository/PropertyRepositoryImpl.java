package com.booking.dataprovider.property.repository;

import com.booking.business.property.model.Property;
import com.booking.business.property.model.PropertyPageableView;
import com.booking.business.property.model.PropertyView;
import com.booking.business.property.repository.PropertyRepository;
import com.booking.dataprovider.property.entity.PropertyJpaEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class PropertyRepositoryImpl implements PropertyRepository {

    private final PropertyJpaEntityRepository delegate;

    public PropertyRepositoryImpl(final PropertyJpaEntityRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public UUID save(final Property property) {
        final var entity = PropertyJpaEntity.from(property);
        return this.delegate.save(entity).getId();
    }

    @Override
    public PropertyPageableView findAll(final int page, final int limit) {
        final var results = delegate.findAll(PageRequest.of(page - 1, limit));
        if (results.isEmpty()) {
            return PropertyPageableView.empty();
        }
        final var content = results.stream().map(
                entity -> new PropertyView(
                        entity.getId(),
                        entity.getName(),
                        entity.getHostName(),
                        entity.getAmenities(),
                        entity.getCreatedAt(),
                        entity.getCheckInTime(),
                        entity.getCheckOutTime(),
                        entity.getDailyRate()
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
        return delegate.findById(id)
                .map(entity -> new PropertyView(
                        entity.getId(),
                        entity.getName(),
                        entity.getHostName(),
                        entity.getAmenities(),
                        entity.getCreatedAt(),
                        entity.getCheckInTime(),
                        entity.getCheckOutTime(),
                        entity.getDailyRate()
                ))
                .or(Optional::empty);
    }
}

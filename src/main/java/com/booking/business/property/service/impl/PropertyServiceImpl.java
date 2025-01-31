package com.booking.business.property.service.impl;

import com.booking.business.property.model.Property;
import com.booking.business.property.model.PropertyPageableView;
import com.booking.business.property.model.PropertyFullView;
import com.booking.business.property.repository.PropertyRepository;
import com.booking.business.property.service.PropertyService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository repository;

    public PropertyServiceImpl(final PropertyRepository repository) {
        this.repository = repository;
    }

    @Override
    public UUID save(final Property property) {
        return this.repository.save(property);
    }

    @Override
    public PropertyPageableView findAll(final int page, final int limit) {
        return this.repository.findAll(page, limit);
    }

    @Override
    public Optional<PropertyFullView> findById(final UUID id) {
        return this.repository.findById(id);
    }

    @Override
    public void validateProperty(final UUID id) {
        //validateBlockedProperty
        validateExistenceProperty(id);
    }

    private void validateExistenceProperty(final UUID id) {
        if (isValidProperty(id)) {
            throw new IllegalArgumentException(
                "Property %s id is not valid".formatted(id)
            );
        }
    }

    private boolean isValidProperty(final UUID id) {
        return !this.repository.isValidProperty(id);
    }
}

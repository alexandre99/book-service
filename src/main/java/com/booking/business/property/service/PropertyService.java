package com.booking.business.property.service;

import com.booking.business.property.model.Property;
import com.booking.business.property.model.PropertyPageableView;
import com.booking.business.property.model.PropertyFullView;

import java.util.Optional;
import java.util.UUID;

public interface PropertyService {

    UUID save(Property property);

    PropertyPageableView findAll(int page, int limit);

    Optional<PropertyFullView> findById(UUID id);

}

package com.booking.business.property.service;

import com.booking.business.property.model.BlockProperty;

import java.util.UUID;

public interface BlockPropertyService {

    UUID save(BlockProperty blockProperty);

    void deleteById(UUID id);

}

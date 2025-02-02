package com.booking.business.property.service.impl;

import com.booking.business.shared.service.OverlapValidationService;
import com.booking.business.property.model.BlockProperty;
import com.booking.business.property.service.BlockPropertyRepository;
import com.booking.business.property.service.BlockPropertyService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BlockPropertyServiceImpl implements BlockPropertyService {

    public static final String PROPERTY_BLOCK_FAILED_THERE_IS_A_BOOKING_IN_THESE_DATES = "Property block failed. There is a booking in these dates";
    public static final String PROPERTY_BLOCK_FAILED_BLOCK_DATES_IS_NOT_AVAILABLE = "Property block failed. Block dates is not available.";
    private final BlockPropertyRepository repository;
    private final OverlapValidationService blockPropertyOverlapValidationService;
    private final OverlapValidationService bookingOverlapValidationService;

    public BlockPropertyServiceImpl(final BlockPropertyRepository repository,
                                    @Qualifier("blockPropertyOverlapValidationServiceImpl")
                                    final OverlapValidationService blockPropertyOverlapValidationService,
                                    @Qualifier("bookingOverlapValidationServiceImpl")
                                    final OverlapValidationService bookingOverlapValidationService) {
        this.repository = repository;
        this.blockPropertyOverlapValidationService = blockPropertyOverlapValidationService;
        this.bookingOverlapValidationService = bookingOverlapValidationService;
    }

    @Override
    public UUID save(final BlockProperty blockProperty) {
        validate(blockProperty);
        return this.repository.save(blockProperty);
    }

    @Override
    public void deleteById(final UUID id) {
        final var isValidToDelete = this.repository.existsById(id);
        if (!isValidToDelete) {
            throw new IllegalArgumentException(
                "Deletion failed. The provided ID %s is invalid".formatted(id)
            );
        }
        this.repository.deleteById(id);
    }

    private void validate(final BlockProperty blockProperty) {
        validateBlockDates(blockProperty);
        validateExistenceBooking(blockProperty);
        validateBlockPropertyOverlap(blockProperty);
    }

    private void validateBlockDates(final BlockProperty blockProperty) {
        if (blockProperty.isStartDateEqualsOrAfterEndDate()) {
            throw new IllegalArgumentException("Block start date must be before end date.");
        }
    }

    private void validateBlockPropertyOverlap(final BlockProperty blockProperty) {
        this.blockPropertyOverlapValidationService.validateOverLap(
                blockProperty.propertyId(),
                blockProperty.startDate(),
                blockProperty.endDate(),
                PROPERTY_BLOCK_FAILED_BLOCK_DATES_IS_NOT_AVAILABLE
        );
    }

    private void validateExistenceBooking(final BlockProperty blockProperty) {
        this.bookingOverlapValidationService.validateOverLap(
                blockProperty.propertyId(),
                blockProperty.startDate(),
                blockProperty.endDate(),
                PROPERTY_BLOCK_FAILED_THERE_IS_A_BOOKING_IN_THESE_DATES
        );
    }

}

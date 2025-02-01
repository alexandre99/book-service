package com.booking.business.property.service.impl;

import com.booking.business.booking.service.BookingService;
import com.booking.business.property.model.BlockProperty;
import com.booking.business.property.service.BlockPropertyRepository;
import com.booking.business.property.service.BlockPropertyService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class BlockPropertyServiceImpl implements BlockPropertyService {

    public static final String PROPERTY_BLOCK_FAILED_THERE_IS_A_BOOKING_IN_THESE_DATES = "Property block failed. There is a booking in these dates";
    private final BlockPropertyRepository repository;
    private final BookingService bookingService;

    public BlockPropertyServiceImpl(final BlockPropertyRepository repository,
                                    final BookingService bookingService) {
        this.repository = repository;
        this.bookingService = bookingService;
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

    @Override
    public void validateOverLap(
            final UUID propertyId,
            final LocalDate startDate,
            final LocalDate endDate,
            final String validationMessage) {
        final var hasOverLap = this.repository.hasOverLap(propertyId, startDate, endDate);
        if (hasOverLap) {
            throw new IllegalStateException(validationMessage);
        }
    }

    private void validateExistenceBooking(final BlockProperty blockProperty) {
        this.bookingService.validateOverLap(
            blockProperty.propertyId(),
            blockProperty.startDate(),
            blockProperty.endDate(),
            PROPERTY_BLOCK_FAILED_THERE_IS_A_BOOKING_IN_THESE_DATES
        );
    }

    private void validateBlockDates(final BlockProperty blockProperty) {
        if (blockProperty.isStartDateEqualsOrAfterEndDate()) {
            throw new IllegalArgumentException("Block start date must be before end date.");
        }
    }

    private void validate(final BlockProperty blockProperty) {
        validateBlockDates(blockProperty);
        validateExistenceBooking(blockProperty);
        validateOverLap(
            blockProperty.propertyId(),
            blockProperty.startDate(),
            blockProperty.endDate(),
            "Property block failed. Block dates is not available."
        );
    }

}

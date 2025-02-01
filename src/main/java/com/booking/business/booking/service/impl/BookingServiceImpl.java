package com.booking.business.booking.service.impl;

import com.booking.business.booking.model.*;
import com.booking.business.booking.repository.BookingRepository;
import com.booking.business.booking.service.BookingService;
import com.booking.business.property.service.BlockPropertyService;
import com.booking.business.property.service.PropertyService;
import com.booking.business.booking.model.State;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {

    public static final String BOOKING_DATES_OVERLAP_MESSAGE = "Booking dates is not available for reservation";
    private final BookingRepository repository;
    private final PropertyService propertyService;
    private final BlockPropertyService blockPropertyService;

    public BookingServiceImpl(final BookingRepository repository,
                              final PropertyService propertyService,
                              final BlockPropertyService blockPropertyService) {
        this.repository = repository;
        this.propertyService = propertyService;
        this.blockPropertyService = blockPropertyService;
    }

    @Override
    public UUID save(final Booking booking) {
        validateStartDateEqualsOrAfterEndDate(booking);
        this.propertyService.validateProperty(booking.propertyId());
        validateOverLaps(booking);
        return this.repository.save(booking);
    }

    @Override
    public Optional<BookingView> findById(final UUID id) {
        return this.repository.findById(id);
    }

    @Override
    public void cancelById(final UUID id) {
        final var isValid = this.repository.existsByIdAndStates(id, List.of(State.ACTIVE));
        validateAction(!isValid, "Cancellation", id);
        this.repository.cancelById(id);
    }

    @Override
    public void rebookById(final UUID id) {
        final var booking = this.repository.findPropertyAndDatesByIdAndCancelState(id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Rebooking failed: The provided ID %s is invalid.".formatted(id)
                ));
        validateOverLap(
            booking.propertyId(), booking.startDate(), booking.endDate(), BOOKING_DATES_OVERLAP_MESSAGE
        );
        this.repository.rebookById(id);
    }

    @Override
    public void deleteById(final UUID id) {
        final var isValid = this.repository.existsByIdAndStates(id, List.of(State.ACTIVE, State.CANCELED));
        validateAction(!isValid, "Deletion", id);
        this.repository.deleteById(id);
    }

    @Override
    public void updateBookingDates(final UUID id,
                                   final LocalDate startDate,
                                   final LocalDate endDate) {
        final var propertyId = this.repository.findPropertyByIdAndBookingActive(id);
        validateAction(propertyId.isEmpty(), "Updating booking dates", id);
        final var booking = new Booking(id, propertyId.get(), startDate, endDate, null);
        validateStartDateEqualsOrAfterEndDate(booking);
        validateOverLaps(booking);
        this.repository.updateBookingDates(id, startDate, endDate);
    }

    @Override
    public void updateGuestDetails(final UUID id, final GuestDetails guestDetails) {
        final var isValidToUpdate = this.repository.existsByIdAndStates(id, List.of(State.ACTIVE));
        validateAction(!isValidToUpdate, "Updating guest details", id);
        this.repository.updateGuestDetails(id, guestDetails);
    }

    @Override
    public void validateOverLap(final UUID propertyId,
                                 final LocalDate startDate,
                                 final LocalDate endDate,
                                 final String validationMessage) {
        final var hasOverLap = this.repository.hasOverLap(propertyId, startDate, endDate);
        if (hasOverLap) {
            throw new IllegalStateException(validationMessage);
        }
    }

    private void validateAction(final boolean isInvalidToApplyAction,
                                final String action,
                                final UUID id) {
        if (isInvalidToApplyAction) {
            throw new IllegalArgumentException(
                "%s failed. The provided ID %s is invalid".formatted(action, id)
            );
        }
    }

    private void validateStartDateEqualsOrAfterEndDate(final Booking booking) {
        if (booking.isStartDateEqualsOrAfterEndDate()) {
            throw new IllegalArgumentException("Booking start date must be before end date.");
        }
    }

    private void validateOverLaps(final Booking booking) {
        validateOverLap(
            booking.propertyId(), booking.startDate(), booking.endDate(), BOOKING_DATES_OVERLAP_MESSAGE
        );
        this.blockPropertyService.validateOverLap(
            booking.propertyId(), booking.startDate(), booking.endDate(), BOOKING_DATES_OVERLAP_MESSAGE
        );
    }

}

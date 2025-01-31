package com.booking.business.booking.service.impl;

import com.booking.business.booking.model.Booking;
import com.booking.business.booking.model.BookingView;
import com.booking.business.booking.model.BookingWithPropertyAndDates;
import com.booking.business.booking.repository.BookingRepository;
import com.booking.business.booking.service.BookingService;
import com.booking.business.property.service.PropertyService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final PropertyService propertyService;

    public BookingServiceImpl(final BookingRepository repository,
                              final PropertyService propertyService) {
        this.repository = repository;
        this.propertyService = propertyService;
    }

    @Override
    public UUID save(final Booking booking) {
        this.propertyService.validateProperty(booking.propertyId());
        validateDates(booking);
        return this.repository.save(booking);
    }

    @Override
    public Optional<BookingView> findById(final UUID id) {
        return this.repository.findById(id);
    }

    @Override
    public void cancelById(final UUID id) {
        this.repository.cancelById(id);
    }

    @Override
    public void rebookById(final UUID id) {
        final var bookingWithPropertyAndDates = this.repository.findPropertyAndDatesByIdAndCancelState(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking id %s is not valid".formatted(id)));
        this.processRebooking(id, bookingWithPropertyAndDates);
    }

    private void processRebooking(final UUID bookingId, final BookingWithPropertyAndDates booking) {
        validateOverLap(booking.propertyId(), booking.startDate(), booking.endDate());
        this.repository.rebookById(bookingId);
    }


    private void validateDates(final Booking booking) {
        validateStartDateEqualsOrAfterEndDate(booking);
        validateOverLap(booking.propertyId(), booking.startDate(), booking.endDate());
    }

    private void validateOverLap(final UUID propertyId,
                                 final LocalDate startDate,
                                 final LocalDate endDate) {
        final var hasOverLap = this.repository.hasOverLap(propertyId, startDate, endDate);
        if (hasOverLap) {
            throw new IllegalStateException("Booking dates is not available for reservation");
        }
    }

    private void validateStartDateEqualsOrAfterEndDate(final Booking booking) {
        if (booking.isStartDateEqualsOrAfterEndDate()) {
            throw new IllegalArgumentException("Booking start date must be before end date.");
        }
    }

}

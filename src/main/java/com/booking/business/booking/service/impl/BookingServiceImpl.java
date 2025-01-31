package com.booking.business.booking.service.impl;

import com.booking.business.booking.model.Booking;
import com.booking.business.booking.model.BookingView;
import com.booking.business.booking.repository.BookingRepository;
import com.booking.business.booking.service.BookingService;
import com.booking.business.property.service.PropertyService;
import org.springframework.stereotype.Service;

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

    private void validateDates(final Booking booking) {
        validateStartDateEqualsOrAfterEndDate(booking);
        validateOverLap(booking);
    }

    private void validateOverLap(final Booking booking) {
        if (hasOverLap(booking)) {
            throw new IllegalStateException("Booking dates is not available for reservation");
        }
    }

    private boolean hasOverLap(final Booking booking) {
        return this.repository.hasOverLap(
                booking.propertyId(), booking.startDate(), booking.endDate());
    }

    private void validateStartDateEqualsOrAfterEndDate(final Booking booking) {
        if (booking.isStartDateEqualsOrAfterEndDate()) {
            throw new IllegalArgumentException("Booking start date must be before end date.");
        }
    }

}

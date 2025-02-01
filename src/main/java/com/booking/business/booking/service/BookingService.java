package com.booking.business.booking.service;

import com.booking.business.booking.model.Booking;
import com.booking.business.booking.model.BookingView;
import com.booking.business.booking.model.GuestDetails;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface BookingService {

    UUID save(Booking booking);

    Optional<BookingView> findById(UUID id);

    void cancelById(UUID id);

    void rebookById(UUID id);

    void deleteById(UUID id);

    void updateBookingDates(UUID id, LocalDate startDate, LocalDate endDate);

    void updateGuestDetails(UUID id, GuestDetails guestDetails);

    void validateOverLap(final UUID propertyId,
                             final LocalDate startDate,
                             final LocalDate endDate,
                            final String validationMessage);

}

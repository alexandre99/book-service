package com.booking.business.booking.repository;

import com.booking.business.booking.model.Booking;
import com.booking.business.booking.model.BookingView;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository {

    UUID save(Booking booking);

    Optional<BookingView> findById(UUID id);

    boolean hasOverLap(UUID propertyId, LocalDate startDate, LocalDate endDate);
}

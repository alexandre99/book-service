package com.booking.business.booking.repository;

import com.booking.business.booking.model.Booking;
import com.booking.business.booking.model.BookingView;
import com.booking.business.booking.model.BookingWithPropertyAndDates;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository {

    UUID save(Booking booking);

    Optional<BookingView> findById(UUID id);

    void cancelById(UUID id);

    void rebookById(UUID id);

    void deleteById(UUID id);

    Optional<BookingWithPropertyAndDates> findPropertyAndDatesByIdAndCancelState(UUID id);

    boolean hasOverLap(UUID propertyId, LocalDate startDate, LocalDate endDate);

    boolean existsById(UUID id);

    Optional<UUID> findPropertyByIdAndBookingActive(UUID id);

    void updateReservationDates(UUID id, LocalDate startDate, LocalDate endDate);
}

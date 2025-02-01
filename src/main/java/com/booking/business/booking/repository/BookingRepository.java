package com.booking.business.booking.repository;

import com.booking.business.booking.model.*;

import java.time.LocalDate;
import java.util.List;
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

    boolean existsByIdAndStates(UUID id, List<State> states);

    Optional<UUID> findPropertyByIdAndBookingActive(UUID id);

    void updateReservationDates(UUID id, LocalDate startDate, LocalDate endDate);

    void updateGuestDetails(UUID id, GuestDetails guestDetails);
}

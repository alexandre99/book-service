package com.booking.business.booking.service;

import com.booking.business.booking.model.Booking;
import com.booking.business.booking.model.BookingView;

import java.util.Optional;
import java.util.UUID;

public interface BookingService {

    UUID save(Booking booking);

    Optional<BookingView> findById(UUID id);

    void cancelById(UUID id);

    void rebookById(UUID id);

}

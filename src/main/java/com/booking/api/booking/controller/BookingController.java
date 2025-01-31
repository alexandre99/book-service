package com.booking.api.booking.controller;

import com.booking.api.booking.dto.BookingRequestDTO;
import com.booking.business.booking.model.Booking;
import com.booking.business.booking.model.BookingView;
import com.booking.business.booking.model.GuestDetails;
import com.booking.business.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RequestMapping("/booking")
@RestController
public class BookingController {

    private final BookingService service;

    public BookingController(final BookingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid
                                       final BookingRequestDTO bookingRequestDTO) {
        final var guestDetails = new GuestDetails(
            bookingRequestDTO.guestDetails().fullName(),
            bookingRequestDTO.guestDetails().email(),
            bookingRequestDTO.guestDetails().phone(),
            bookingRequestDTO.guestDetails().numberOfAdults(),
            bookingRequestDTO.guestDetails().numberOfChildren(),
            bookingRequestDTO.guestDetails().numberOfInfants(),
            bookingRequestDTO.guestDetails().specialRequests()
        );
        final var booking = new Booking(
            null,
            bookingRequestDTO.propertyId(),
            bookingRequestDTO.startDate(),
            bookingRequestDTO.endDate(),
            guestDetails
        );
        final var savedBookingId = this.service.save(booking);

        final var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedBookingId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingView> findById(@PathVariable("id")
                                                final UUID id) {
        final var bookingView = this.service.findById(id);
        return bookingView.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

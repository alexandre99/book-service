package com.booking.business;

import com.booking.business.booking.model.Booking;
import com.booking.business.booking.model.GuestDetails;
import com.booking.business.booking.model.BookingWithPropertyAndDates;
import com.booking.business.booking.model.State;
import com.booking.business.booking.repository.BookingRepository;
import com.booking.business.booking.service.impl.BookingServiceImpl;
import com.booking.business.property.service.PropertyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private PropertyService propertyService;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void shouldSaveBooking() {
        //given
        final var booking = getBooking(
            LocalDate.now(), LocalDate.now().plusDays(1)
        );
        //when
        this.bookingService.save(booking);
        //then
        verify(this.propertyService).validateProperty(booking.propertyId());
        verify(this.propertyService).validateProperty(any(UUID.class));

        verify(this.bookingRepository).hasOverLap(
            booking.propertyId(), booking.startDate(), booking.endDate()
        );
        verify(this.bookingRepository).hasOverLap(
            any(UUID.class), any(LocalDate.class), any(LocalDate.class)
        );

        verify(this.bookingRepository).save(booking);
        verify(this.bookingRepository).save(any(Booking.class));
    }

    @Test
    void shouldNotSaveBookingWhenStartDateIsEqualsToEndDate() {
        //given
        final var booking = getBooking(
            LocalDate.now(), LocalDate.now()
        );
        //when then
        verifyWhenStartDateIsInvalid(booking);
    }

    @Test
    void shouldNotSaveBookingWhenStartDateIsAfterToEndDate() {
        //given
        final var booking = getBooking(
            LocalDate.now().plusDays(1), LocalDate.now()
        );
        //when then
        verifyWhenStartDateIsInvalid(booking);
    }

    @Test
    void shouldNotSaveBookingWhenThereIsAnOverlap() {
        //given
        final var booking = getBooking(
            LocalDate.now(), LocalDate.now().plusDays(1)
        );
        when(this.bookingRepository.hasOverLap(
            booking.propertyId(), booking.startDate(), booking.endDate()
        )).thenReturn(true);
        //when
        assertThrows(IllegalStateException.class,
                () -> this.bookingService.save(booking));

        verify(this.propertyService).validateProperty(booking.propertyId());
        verify(this.propertyService).validateProperty(any(UUID.class));

        verify(this.bookingRepository).hasOverLap(
            booking.propertyId(), booking.startDate(), booking.endDate()
        );
        verify(this.bookingRepository).hasOverLap(
            any(UUID.class), any(LocalDate.class), any(LocalDate.class)
        );

        verify(this.bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldNotSaveBookingWhenPropertyIsInvalid() {
        //given
        final var booking = getBooking(
            LocalDate.now(), LocalDate.now().plusDays(1)
        );
        doThrow(IllegalArgumentException.class)
                .when(this.propertyService).validateProperty(booking.propertyId());

        //when
        assertThrows(IllegalArgumentException.class,
                () -> this.bookingService.save(booking));

        verify(this.bookingRepository, never()).hasOverLap(
                any(UUID.class), any(LocalDate.class), any(LocalDate.class)
        );

        verify(this.bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldNotCancelABookingWhenBookingIdIsNoValid() {
        //given
        final var bookingId = UUID.randomUUID();
        when(this.bookingRepository.existsByIdAndStates(bookingId, List.of(State.ACTIVE)))
                .thenReturn(false);

        //when then
        assertThrows(IllegalArgumentException.class,
                () -> this.bookingService.cancelById(bookingId));
    }

    @Test
    void shouldCancelBooking() {
        //given
        final var bookingId = UUID.randomUUID();
        when(this.bookingRepository.existsByIdAndStates(bookingId, List.of(State.ACTIVE)))
                .thenReturn(true);
        //when
        this.bookingService.cancelById(bookingId);
        //
        verify(this.bookingRepository).cancelById(bookingId);
        verify(this.bookingRepository).cancelById(any(UUID.class));
    }

    @Test
    void shouldNotRebookWhenBookingIsInvalid() {
        //given
        final var bookingId = UUID.randomUUID();
        when(this.bookingRepository.findPropertyAndDatesByIdAndCancelState(bookingId))
                .thenReturn(Optional.empty());
        //when
        assertThrows(IllegalArgumentException.class,
                () -> this.bookingService.rebookById(bookingId));
        //then
        verify(this.bookingRepository, never()).rebookById(any(UUID.class));
    }

    @Test
    void shouldRebook() {
        //given
        final var bookingId = UUID.randomUUID();
        final var bookingWithPropertyAndDates = new BookingWithPropertyAndDates(
            UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(1)
        );
        when(this.bookingRepository.findPropertyAndDatesByIdAndCancelState(bookingId))
                .thenReturn(Optional.of(bookingWithPropertyAndDates));
        //when
        this.bookingService.rebookById(bookingId);

        //then
        verify(this.bookingRepository).rebookById(bookingId);
        verify(this.bookingRepository).rebookById(any(UUID.class));
    }

    @Test
    void shouldNotDeleteWhenBookingIdIsNoValid() {
        //given
        final var bookingId = UUID.randomUUID();
        when(this.bookingRepository.existsByIdAndStates(bookingId, List.of(State.ACTIVE, State.CANCELED)))
                .thenReturn(false);

        //when then
        assertThrows(IllegalArgumentException.class,
                () -> this.bookingService.deleteById(bookingId));
    }

    @Test
    void shouldDeleteBooking() {
        //given
        final var bookingId = UUID.randomUUID();
        when(this.bookingRepository.existsByIdAndStates(bookingId, List.of(State.ACTIVE, State.CANCELED)))
                .thenReturn(true);
        //when
        this.bookingService.deleteById(bookingId);
        //
        verify(this.bookingRepository).deleteById(bookingId);
        verify(this.bookingRepository).deleteById(any(UUID.class));
    }

    @Test
    void shouldNotUpdateBookingDatesWhenBookingIsNotValid() {
        //given
        final var bookingId = UUID.randomUUID();
        when(this.bookingRepository.findPropertyByIdAndBookingActive(bookingId))
                .thenReturn(Optional.empty());
        //when then
        assertThrows(IllegalArgumentException.class,
                () -> this.bookingService.updateBookingDates(
                    bookingId, LocalDate.now(), LocalDate.now()
                ));

        verify(this.bookingRepository, never()).updateBookingDates(
            any(UUID.class), any(LocalDate.class), any(LocalDate.class)
        );
    }

    @Test
    void shouldNotUpdateReservationWhenStartDateIsEqualsToEndDate() {
        //given
        final var bookingId = UUID.randomUUID();
        final var propertyId = UUID.randomUUID();
        when(this.bookingRepository.findPropertyByIdAndBookingActive(bookingId))
                .thenReturn(Optional.of(propertyId));
        //when then
        assertThrows(IllegalArgumentException.class,
            () -> this.bookingService.updateBookingDates(bookingId, LocalDate.now(), LocalDate.now()));

        verify(this.bookingRepository, never()).updateBookingDates(
            any(UUID.class), any(LocalDate.class), any(LocalDate.class)
        );
    }

    @Test
    void shouldNotUpdateReservationWhenStartDateIsAfterToEndDate() {
        //given
        final var bookingId = UUID.randomUUID();
        final var propertyId = UUID.randomUUID();
        when(this.bookingRepository.findPropertyByIdAndBookingActive(bookingId))
                .thenReturn(Optional.of(propertyId));
        //when then
        assertThrows(IllegalArgumentException.class,
            () -> this.bookingService.updateBookingDates(
                bookingId, LocalDate.now().plusDays(1), LocalDate.now())
        );

        verify(this.bookingRepository, never()).updateBookingDates(
            any(UUID.class), any(LocalDate.class), any(LocalDate.class)
        );
    }

    @Test
    void shouldNotUpdateReservationWhenThereIsAnOverlap() {
        //given
        final var startDate = LocalDate.now();
        final var endDate = LocalDate.now().plusDays(1);
        final var bookingId = UUID.randomUUID();
        final var propertyId = UUID.randomUUID();
        when(this.bookingRepository.hasOverLap(
            propertyId, startDate, endDate
        )).thenReturn(true);
        when(this.bookingRepository.findPropertyByIdAndBookingActive(bookingId))
                .thenReturn(Optional.of(propertyId));
        //when
        assertThrows(IllegalStateException.class,
            () -> this.bookingService.updateBookingDates(
                bookingId, startDate, endDate
            ));

        verify(this.bookingRepository, never()).updateBookingDates(
            any(UUID.class), any(LocalDate.class), any(LocalDate.class)
        );
    }

    @Test
    void shouldNotUpdateGuestDetailsWhenBookingIdIsInvalid() {
        //given
        final var bookingId = UUID.randomUUID();
        final var guestDetails = new GuestDetails(
            "fullName",
                "email",
                "phone",
                1,
                0,
                0,
                "specialRequests"
        );
        when(this.bookingRepository.existsByIdAndStates(bookingId, List.of(State.ACTIVE)))
                .thenReturn(false);
        //when then
        assertThrows(IllegalArgumentException.class,
                () -> this.bookingService.updateGuestDetails(
                    bookingId, guestDetails
                ));

        verify(this.bookingRepository, never()).updateGuestDetails(
                any(UUID.class), any(GuestDetails.class)
        );
    }

    @Test
    void shouldUpdateGuestDetails() {
        //given
        final var bookingId = UUID.randomUUID();
        final var guestDetails = new GuestDetails(
                "fullName",
                "email",
                "phone",
                1,
                0,
                0,
                "specialRequests"
        );
        when(this.bookingRepository.existsByIdAndStates(bookingId, List.of(State.ACTIVE)))
                .thenReturn(true);
        //when
        this.bookingService.updateGuestDetails(
            bookingId, guestDetails
        );
        //then
        verify(this.bookingRepository).updateGuestDetails(bookingId, guestDetails);
    }

    private void verifyWhenStartDateIsInvalid(Booking booking) {
        assertThrows(IllegalArgumentException.class,
                () -> this.bookingService.save(booking));

        verify(this.propertyService).validateProperty(booking.propertyId());
        verify(this.propertyService).validateProperty(any(UUID.class));

        verify(this.bookingRepository, never()).hasOverLap(
                any(UUID.class), any(LocalDate.class), any(LocalDate.class)
        );

        verify(this.bookingRepository, never()).save(any(Booking.class));
    }

    private static Booking getBooking(final LocalDate startDate,
                                      final LocalDate endDate) {
        final var guestDetails = new GuestDetails(
            UUID.randomUUID().toString(),
            "foo@gmai.com",
            "+5591232265896",
            1,
            0,
            0,
            "specialRequests"
        );
        return new Booking(
          null,
            UUID.randomUUID(),
            startDate,
            endDate,
            guestDetails
        );
    }

}

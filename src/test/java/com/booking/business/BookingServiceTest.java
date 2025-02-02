package com.booking.business;

import com.booking.business.booking.model.Booking;
import com.booking.business.booking.model.GuestDetails;
import com.booking.business.booking.model.BookingWithPropertyAndDates;
import com.booking.business.booking.model.State;
import com.booking.business.booking.repository.BookingRepository;
import com.booking.business.booking.service.BookingService;
import com.booking.business.booking.service.impl.BookingServiceImpl;
import com.booking.business.property.service.PropertyService;
import com.booking.business.shared.service.OverlapValidationService;
import com.booking.business.shared.service.impl.BlockPropertyOverlapValidationServiceImpl;
import com.booking.business.shared.service.impl.BookingOverlapValidationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.booking.business.booking.service.impl.BookingServiceImpl.BOOKING_DATES_OVERLAP_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    private final BookingRepository bookingRepository = mock(BookingRepository.class);
    private final PropertyService propertyService = mock(PropertyService.class);
    private final OverlapValidationService blockPropertyOverlapValidationService = mock(BlockPropertyOverlapValidationServiceImpl.class);
    private final OverlapValidationService bookingOverlapValidationService = mock(BookingOverlapValidationServiceImpl.class);
    private BookingService bookingService;

    @BeforeEach
    void setup() {
        this.bookingService = new BookingServiceImpl(
            bookingRepository,
            propertyService,
            blockPropertyOverlapValidationService,
            bookingOverlapValidationService
        );
    }

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
        verify(this.propertyService).validateProperty(any());

        verify(this.bookingOverlapValidationService).validateOverLap(
            booking.propertyId(), booking.startDate(), booking.endDate(), BOOKING_DATES_OVERLAP_MESSAGE
        );
        verify(this.bookingOverlapValidationService).validateOverLap(
            any(), any(), any(), anyString()
        );

        verify(this.blockPropertyOverlapValidationService).validateOverLap(
                booking.propertyId(), booking.startDate(), booking.endDate(), BOOKING_DATES_OVERLAP_MESSAGE
        );
        verify(this.blockPropertyOverlapValidationService).validateOverLap(
                any(), any(), any(), anyString()
        );

        verify(this.bookingRepository).save(booking);
        verify(this.bookingRepository).save(any());
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
        doThrow(IllegalStateException.class).when(this.bookingOverlapValidationService).validateOverLap(
            booking.propertyId(), booking.startDate(), booking.endDate(), BOOKING_DATES_OVERLAP_MESSAGE
        );

        //when
        assertThrows(IllegalStateException.class,
                () -> this.bookingService.save(booking));

        verify(this.propertyService).validateProperty(booking.propertyId());
        verify(this.propertyService).validateProperty(any(UUID.class));

        verify(this.bookingOverlapValidationService).validateOverLap(
                booking.propertyId(), booking.startDate(), booking.endDate(), BOOKING_DATES_OVERLAP_MESSAGE
        );
        verify(this.bookingOverlapValidationService).validateOverLap(
                any(), any(), any(), anyString()
        );

        verify(this.blockPropertyOverlapValidationService, never()).validateOverLap(
                any(), any(), any(), anyString()
        );

        verify(this.bookingRepository, never()).save(any());
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

        verify(this.bookingOverlapValidationService, never()).validateOverLap(
                any(), any(), any(), anyString()
        );

        verify(this.blockPropertyOverlapValidationService, never()).validateOverLap(
                any(), any(), any(), anyString()
        );
        verify(this.bookingRepository, never()).save(any());
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
        verify(this.bookingRepository).cancelById(any());
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
        verify(this.bookingRepository, never()).rebookById(any());
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
        verify(this.bookingRepository).rebookById(any());
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
        verify(this.bookingRepository).deleteById(any());
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
            any(), any(), any()
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
            any(), any(), any()
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
            any(), any(), any()
        );
    }

    @Test
    void shouldNotUpdateReservationWhenThereIsAnOverlap() {
        //given
        final var startDate = LocalDate.now();
        final var endDate = LocalDate.now().plusDays(1);
        final var bookingId = UUID.randomUUID();
        final var propertyId = UUID.randomUUID();

        doThrow(IllegalStateException.class).when(this.bookingOverlapValidationService).validateOverLap(
            propertyId, startDate, endDate, BOOKING_DATES_OVERLAP_MESSAGE
        );

        when(this.bookingRepository.findPropertyByIdAndBookingActive(bookingId))
                .thenReturn(Optional.of(propertyId));
        //when
        assertThrows(IllegalStateException.class,
            () -> this.bookingService.updateBookingDates(
                bookingId, startDate, endDate
            ));

        verify(this.bookingOverlapValidationService).validateOverLap(
            propertyId, startDate, endDate, BOOKING_DATES_OVERLAP_MESSAGE
        );
        verify(this.bookingOverlapValidationService).validateOverLap(
                any(), any(), any(), anyString()
        );

        verify(this.blockPropertyOverlapValidationService, never()).validateOverLap(
                any(), any(), any(), anyString()
        );

        verify(this.bookingRepository, never()).updateBookingDates(
            any(), any(), any()
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
            any(), any()
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

    @Test
    void shouldNotSaveBookingWhenThereIsAnOverlapWithBlockProperty() {
        //given
        final var booking = getBooking(
            LocalDate.now(), LocalDate.now().plusDays(1)
        );
        doThrow(IllegalStateException.class).when(this.blockPropertyOverlapValidationService).validateOverLap(
            booking.propertyId(), booking.startDate(), booking.endDate(), BOOKING_DATES_OVERLAP_MESSAGE
        );

        //when
        assertThrows(IllegalStateException.class,
                () -> this.bookingService.save(booking));

        verify(this.propertyService).validateProperty(booking.propertyId());
        verify(this.propertyService).validateProperty(any(UUID.class));

        verify(this.bookingOverlapValidationService).validateOverLap(
                booking.propertyId(), booking.startDate(), booking.endDate(), BOOKING_DATES_OVERLAP_MESSAGE
        );
        verify(this.bookingOverlapValidationService).validateOverLap(
                any(), any(), any(), anyString()
        );

        verify(this.blockPropertyOverlapValidationService).validateOverLap(
                booking.propertyId(), booking.startDate(), booking.endDate(), BOOKING_DATES_OVERLAP_MESSAGE
        );
        verify(this.blockPropertyOverlapValidationService).validateOverLap(
                any(), any(), any(), anyString()
        );

        verify(this.bookingRepository, never()).save(any());
    }

    private void verifyWhenStartDateIsInvalid(Booking booking) {
        assertThrows(IllegalArgumentException.class,
                () -> this.bookingService.save(booking));

        verify(this.propertyService, never()).validateProperty(any());

        verify(this.bookingOverlapValidationService, never()).validateOverLap(
                any(), any(), any(), anyString()
        );

        verify(this.blockPropertyOverlapValidationService, never()).validateOverLap(
                any(), any(), any(), anyString()
        );

        verify(this.bookingRepository, never()).save(any());
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

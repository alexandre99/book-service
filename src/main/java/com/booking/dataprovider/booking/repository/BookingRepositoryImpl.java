package com.booking.dataprovider.booking.repository;

import com.booking.business.booking.model.Booking;
import com.booking.business.booking.model.BookingView;
import com.booking.business.booking.model.State;
import com.booking.business.booking.model.BookingWithPropertyAndDates;
import com.booking.business.booking.repository.BookingRepository;
import com.booking.business.property.model.BookedPropertyDetailsView;
import com.booking.dataprovider.booking.entity.BookingJpaEntity;
import com.booking.dataprovider.booking.model.GuestDetails;
import com.booking.dataprovider.property.entity.PropertyJpaEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BookingRepositoryImpl implements BookingRepository {

    private final BookingJpaEntityRepository delegate;

    public BookingRepositoryImpl(final BookingJpaEntityRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public UUID save(final Booking booking) {
        final var propertyEntity = new PropertyJpaEntity(
            booking.propertyId()
        );
        final var guestDetails = GuestDetails.from(booking.guestDetails());
        final var entity = BookingJpaEntity.from(booking, propertyEntity, guestDetails);
        return this.delegate.save(entity).getId();
    }

    @Override
    public Optional<BookingView> findById(final UUID id) {
        return this.delegate.findByIdAndStateNot(id, State.DELETED)
                .flatMap(entity -> {
                    final var propertyDetails = new BookedPropertyDetailsView(
                      entity.getProperty().getId(),
                      entity.getProperty().getName()
                    );
                    final var guestDetails = new com.booking.business.booking.model.GuestDetails(
                        entity.getGuestDetails().fullName(),
                        entity.getGuestDetails().email(),
                        entity.getGuestDetails().phone(),
                        entity.getGuestDetails().numberOfAdults(),
                        entity.getGuestDetails().numberOfChildren(),
                        entity.getGuestDetails().numberOfInfants(),
                        entity.getGuestDetails().specialRequests()
                    );
                    return Optional.of(new BookingView(
                        entity.getId(),
                        entity.getStartDate(),
                        entity.getEndDate(),
                        entity.getState(),
                        guestDetails,
                        propertyDetails
                    ));
                });
    }

    @Transactional
    @Override
    public void cancelById(final UUID id) {
        this.delegate.cancelById(id, State.CANCELED);
    }

    @Transactional
    @Override
    public void rebookById(final UUID id) {
        this.delegate.rebookById(id, State.ACTIVE);
    }

    @Transactional
    @Override
    public void deleteById(final UUID id) {
        this.delegate.deleteById(id, State.DELETED);
    }

    @Override
    public Optional<BookingWithPropertyAndDates> findPropertyAndDatesByIdAndCancelState(final UUID id) {
        return this.delegate.findPropertyAndDatesByIdAndCancelState(id, State.CANCELED)
                .map(p -> new BookingWithPropertyAndDates(
                        p.getPropertyId(), p.getStartDate(), p.getEndDate()
                    )
                );
    }

    @Override
    public boolean hasOverLap(final UUID propertyId,
                              final LocalDate startDate,
                              final LocalDate endDate) {
        return this.delegate.hasOverlap(
            propertyId, startDate, endDate, State.ACTIVE
        );
    }

    @Override
    public boolean existsById(final UUID id) {
        return this.delegate.existsByIdAndStateNot(id, State.DELETED);
    }

    @Override
    public Optional<UUID> findPropertyByIdAndBookingActive(final UUID id) {
        return this.delegate.findPropertyByIdAndBookingActive(id, State.ACTIVE);
    }

    @Transactional
    @Override
    public void updateReservationDates(final UUID id,
                                       final LocalDate startDate,
                                       final LocalDate endDate) {
        this.delegate.updateReservationDates(id, startDate, endDate);
    }
}

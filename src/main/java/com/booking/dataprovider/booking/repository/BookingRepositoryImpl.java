package com.booking.dataprovider.booking.repository;

import com.booking.business.booking.model.Booking;
import com.booking.business.booking.model.BookingView;
import com.booking.business.booking.repository.BookingRepository;
import com.booking.business.property.model.BookedPropertyDetailsView;
import com.booking.dataprovider.booking.entity.BookingJpaEntity;
import com.booking.dataprovider.booking.model.GuestDetails;
import com.booking.dataprovider.property.entity.PropertyJpaEntity;
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
        return this.delegate.findById(id)
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
                        entity.isCanceled(),
                        guestDetails,
                        propertyDetails
                    ));
                });
    }

    @Override
    public boolean hasOverLap(final UUID propertyId,
                              final LocalDate startDate,
                              final LocalDate endDate) {
        return this.delegate.hasOverlap(
            propertyId, startDate, endDate
        );
    }
}

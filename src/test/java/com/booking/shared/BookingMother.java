package com.booking.shared;

import com.booking.business.booking.model.State;
import com.booking.dataprovider.booking.entity.BookingJpaEntity;
import com.booking.dataprovider.booking.model.GuestDetails;
import com.booking.dataprovider.booking.repository.BookingJpaEntityRepository;
import com.booking.dataprovider.property.entity.PropertyJpaEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class BookingMother {

    private final BookingJpaEntityRepository repository;

    public BookingMother(final BookingJpaEntityRepository repository) {
        this.repository = repository;
    }

    public BookingJpaEntity createBookingEntity(final UUID propertyId,
                                                final State state) {
        final var entity = new BookingJpaEntity(
                null,
                new PropertyJpaEntity(propertyId),
                new GuestDetails(
                        "Joe Doe",
                        "joedoe@gmai.com",
                        "+5591232265896",
                        1,
                        0,
                        0,
                        "specialRequests"
                ),
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                state
        );
        return repository.save(entity);
    }

}

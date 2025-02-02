package com.booking.dataprovider.shared.repository;

import com.booking.business.shared.repository.OverlapRepository;
import com.booking.business.booking.model.State;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public class BookingOverlapRepositoryImpl implements OverlapRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean hasOverlap(final UUID propertyId,
                              final LocalDate startDate,
                              final LocalDate endDate) {
        final var query = """
            SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
            FROM Booking b
            WHERE b.property.id = :propertyId
              AND b.startDate < :endDate
              AND b.endDate > :startDate
              AND b.state = :active
            """;
        return this.entityManager.createQuery(query, Boolean.class)
                .setParameter("propertyId", propertyId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("active", State.ACTIVE)
                .getSingleResult();
    }
}
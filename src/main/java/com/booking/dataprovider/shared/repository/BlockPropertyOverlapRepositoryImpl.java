package com.booking.dataprovider.shared.repository;

import com.booking.business.shared.repository.OverlapRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public class BlockPropertyOverlapRepositoryImpl implements OverlapRepository {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean hasOverlap(final UUID propertyId,
                              final LocalDate startDate,
                              final LocalDate endDate) {
        final var query = """
            SELECT CASE WHEN COUNT(bp) > 0 THEN true ELSE false END
            FROM BlockProperty bp
            WHERE bp.property.id = :propertyId
              AND bp.startDate < :endDate
              AND bp.endDate > :startDate
              AND bp.deleted = FALSE
            """;
        return this.entityManager.createQuery(query, Boolean.class)
                .setParameter("propertyId", propertyId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getSingleResult();
    }
}

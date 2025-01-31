package com.booking.dataprovider.booking.repository;

import com.booking.dataprovider.booking.entity.BookingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface BookingJpaEntityRepository extends JpaRepository<BookingJpaEntity, UUID> {

    @Query(value = """
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.property.id = :propertyId
            AND b.startDate < :endDate
            AND b.endDate > :startDate
            AND b.canceled = FALSE
        """)
    boolean hasOverlap(@Param("propertyId")
                       final UUID propertyId,
                       @Param("startDate")
                       final LocalDate start,
                       @Param("endDate")
                       final LocalDate end);

}

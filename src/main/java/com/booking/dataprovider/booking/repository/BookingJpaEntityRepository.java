package com.booking.dataprovider.booking.repository;

import com.booking.business.booking.model.State;
import com.booking.business.booking.projection.BookingWithPropertyAndDates;
import com.booking.dataprovider.booking.entity.BookingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingJpaEntityRepository extends JpaRepository<BookingJpaEntity, UUID> {

    @Query(value = """
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.property.id = :propertyId
            AND b.startDate < :endDate
            AND b.endDate > :startDate
            AND state = 'ACTIVE'
        """)
    boolean hasOverlap(@Param("propertyId")
                       UUID propertyId,
                       @Param("startDate")
                       LocalDate start,
                       @Param("endDate")
                       LocalDate end);

    @Modifying
    @Query("""
            UPDATE Booking b SET b.state = :canceled WHERE b.id =:id
        """)
    void cancelById(@Param("id")
                    UUID id,
                    @Param("canceled")
                    State canceled);

    @Modifying
    @Query("""
            UPDATE Booking b SET b.state = :active WHERE b.id =:id
        """)
    void rebookById(@Param("id")
                    UUID id,
                    @Param("active")
                    State active);

    @Modifying
    @Query("""
        SELECT b.property.id AS propertyId, b.startDate, b.endDate
        FROM Booking b
        WHERE b.id = :id AND b.state = :canceled
    """)
    Optional<BookingWithPropertyAndDates> findPropertyAndDatesByIdAndCancelState(
            @Param("id")
            UUID id,
            @Param("canceled")
            State canceled
    );


}

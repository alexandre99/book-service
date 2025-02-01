package com.booking.dataprovider.booking.repository;

import com.booking.business.booking.model.State;
import com.booking.dataprovider.booking.entity.BookingJpaEntity;
import com.booking.dataprovider.booking.projection.BookingWithPropertyAndDatesProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingJpaEntityRepository extends JpaRepository<BookingJpaEntity, UUID> {

    @Query(value = """
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.property.id = :propertyId
            AND b.startDate < :endDate
            AND b.endDate > :startDate
            AND state = :active
        """)
    boolean hasOverlap(@Param("propertyId")
                       UUID propertyId,
                       @Param("startDate")
                       LocalDate start,
                       @Param("endDate")
                       LocalDate end,
                       @Param("active")
                       State active);

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

    @Query("""
        SELECT b.property.id AS propertyId, b.startDate AS startDate, b.endDate as endDate
        FROM Booking b
        WHERE b.id = :id AND b.state = :canceled
    """)
    Optional<BookingWithPropertyAndDatesProjection> findPropertyAndDatesByIdAndCancelState(
            @Param("id")
            UUID id,
            @Param("canceled")
            State canceled
    );

    @Modifying
    @Query("""
            UPDATE Booking b SET b.state = :deleted WHERE b.id =:id
        """)
    void deleteById(@Param("id") UUID id,
                    @Param("deleted")
                    State state);

    Optional<BookingJpaEntity> findByIdAndStateNot(UUID id, State state);

    boolean existsByIdAndStateIn(UUID id, List<State> state);

    @Query("""
            SELECT b.property.id FROM Booking b
            WHERE b.id =:id AND b.state =:active
        """)
    Optional<UUID> findPropertyByIdAndBookingActive(@Param("id") UUID id,
                                             @Param("active") State active);

    @Modifying
    @Query("""
            UPDATE Booking b SET b.startDate = :startDate, b.endDate = :endDate  WHERE b.id =:id
        """)
    void updateReservationDates(@Param("id")
                                UUID id,
                                @Param("startDate")
                                LocalDate startDate,
                                @Param("endDate")
                                LocalDate localDate);
}

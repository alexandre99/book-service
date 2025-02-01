package com.booking.dataprovider.property.repository;

import com.booking.dataprovider.property.entity.BlockPropertyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface BlockPropertyJpaEntityRepository extends JpaRepository<BlockPropertyJpaEntity, UUID> {

    @Modifying
    @Query("""
            UPDATE BlockProperty bp SET bp.deleted = TRUE WHERE bp.id =:id
        """)
    @Override
    void deleteById(@Param("id") UUID id);

    @Query(value = """
            SELECT COUNT(b) > 0 FROM BlockProperty bp
            WHERE bp.property.id = :propertyId
            AND bp.startDate < :endDate
            AND bp.endDate > :startDate
            AND deleted = FALSE
        """)
    boolean hasOverlap(@Param("propertyId")
                       UUID propertyId,
                       @Param("startDate")
                       LocalDate start,
                       @Param("endDate")
                       LocalDate end);

    @Query("""
            SELECT COUNT(b) > 0 FROM BlockProperty bp
            WHERE bp.id =:id AND bp.deleted = FALSE
        """)
    @Override
    boolean existsById(@Param("id") UUID id);
}

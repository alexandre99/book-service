package com.booking.dataprovider.property.repository;

import com.booking.dataprovider.property.entity.BlockPropertyJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BlockPropertyJpaEntityRepository extends JpaRepository<BlockPropertyJpaEntity, UUID> {

    @Modifying
    @Query("""
            UPDATE BlockProperty bp SET bp.deleted = TRUE WHERE bp.id =:id
        """)
    @Override
    void deleteById(@Param("id") UUID id);

    @Query("""
            SELECT COUNT(bp) > 0 FROM BlockProperty bp
            WHERE bp.id =:id AND bp.deleted = FALSE
        """)
    @Override
    boolean existsById(@Param("id") UUID id);

    @Query("""
            SELECT bp FROM BlockProperty bp
            WHERE bp.id =:id AND bp.deleted = FALSE
        """)
    Optional<BlockPropertyJpaEntity> findBlockPropertyById(@Param("id")
                                                         UUID id);

    @Query("""
            SELECT bp FROM BlockProperty bp
            WHERE bp.deleted = FALSE
        """)
    Page<BlockPropertyJpaEntity> findAllBlockProperty(Pageable pageable);
}

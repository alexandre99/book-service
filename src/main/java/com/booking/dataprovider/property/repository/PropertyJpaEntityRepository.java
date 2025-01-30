package com.booking.dataprovider.property.repository;

import com.booking.dataprovider.property.entity.PropertyJpaEntity;
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
public interface PropertyJpaEntityRepository extends JpaRepository<PropertyJpaEntity, UUID> {

    @Modifying
    @Query("UPDATE PropertyJpaEntity p SET p.enable = false WHERE p.id =:id")
    void disable(@Param("id") UUID id);

    Optional<PropertyJpaEntity> findByIdAndEnableTrue(UUID uuid);

    Page<PropertyJpaEntity> findByEnableTrue(Pageable pageable);

}

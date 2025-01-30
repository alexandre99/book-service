package com.booking.dataprovider.property.repository;

import com.booking.dataprovider.property.entity.PropertyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PropertyJpaEntityRepository extends JpaRepository<PropertyJpaEntity, UUID> {

}

package com.booking.dataprovider.booking.repository;

import com.booking.dataprovider.booking.entity.BookingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookingJpaEntityRepository extends JpaRepository<BookingJpaEntity, UUID> {
}

package com.booking.dataprovider.booking.entity;

import com.booking.dataprovider.booking.model.GuestDetails;
import com.booking.dataprovider.property.entity.PropertyJpaEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "booking")
public class BookingJpaEntity {

    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private PropertyJpaEntity property;
    @Embedded
    private GuestDetails guestDetails;
    @Column(nullable = false)
    @CreationTimestamp
    private Instant createdAt;
    @Column(nullable = false)
    @UpdateTimestamp
    private Instant updatedAt;
    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalDate endDate;
    private boolean canceled;

    public BookingJpaEntity() {}

    public BookingJpaEntity(
            final UUID id,
            final PropertyJpaEntity property,
            final GuestDetails guestDetails,
            final LocalDate startDate,
            final LocalDate endDate
    ) {
        this.id = id;
        this.property = property;
        this.guestDetails = guestDetails;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public UUID getId() {
        return id;
    }

    public PropertyJpaEntity getProperty() {
        return property;
    }

    public GuestDetails getGuestDetails() {
        return guestDetails;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BookingJpaEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

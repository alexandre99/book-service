package com.booking.dataprovider.booking.entity;

import com.booking.business.property.model.Property;
import com.booking.dataprovider.booking.model.GuestDetails;
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
    private Property property;
    @Embedded
    private GuestDetails guestDetails;
    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;
    private LocalDate start;
    private LocalDate end;
    private boolean canceled;

    public BookingJpaEntity() {}

    public BookingJpaEntity(
            final UUID id,
            final Property property,
            final GuestDetails guestDetails,
            final LocalDate start,
            final LocalDate end
    ) {
        this.id = id;
        this.property = property;
        this.guestDetails = guestDetails;
        this.start = start;
        this.end = end;
    }

    public UUID getId() {
        return id;
    }

    public Property getProperty() {
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

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
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

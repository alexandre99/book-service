package com.booking.dataprovider.property.entity;

import com.booking.business.property.model.BlockProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "BlockProperty")
@Table(name = "block_property")
public class BlockPropertyJpaEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private PropertyJpaEntity property;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private Instant updatedAt;

    private boolean deleted;

    public BlockPropertyJpaEntity(
            final UUID id,
            final PropertyJpaEntity property,
            final LocalDate startDate,
            final LocalDate endDate) {
        this.id = id;
        this.property = property;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public UUID getId() {
        return id;
    }

    public PropertyJpaEntity getProperty() {
        return property;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BlockPropertyJpaEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static BlockPropertyJpaEntity from(final BlockProperty blockProperty) {
        final var propertyJpaEntity = new PropertyJpaEntity(blockProperty.propertyId());
        return new BlockPropertyJpaEntity(
          blockProperty.id(),
          propertyJpaEntity,
          blockProperty.startDate(),
          blockProperty.endDate()
        );
    }

}

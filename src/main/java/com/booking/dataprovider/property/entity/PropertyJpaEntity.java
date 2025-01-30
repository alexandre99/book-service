package com.booking.dataprovider.property.entity;

import com.booking.business.property.model.Property;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "property")
public class PropertyJpaEntity {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "owner_id")
    private UUID ownerId;
    private String name;
    private Set<String> amenities;
    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;
    private boolean enable;

    public PropertyJpaEntity() {}

    public PropertyJpaEntity(
            final UUID id,
            final UUID ownerId,
            final String name,
            final Set<String> amenities) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.amenities = amenities;
        this.enable = true;
    }

    public boolean isEnable() {
        return enable;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Set<String> getAmenities() {
        return amenities;
    }

    public String getName() {
        return name;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PropertyJpaEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static PropertyJpaEntity from(final Property property) {
        return new PropertyJpaEntity(
                property.id(),
                property.ownerId(),
                property.name(),
                property.amenities()
        );
    }
}

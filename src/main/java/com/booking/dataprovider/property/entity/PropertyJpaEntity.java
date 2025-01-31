package com.booking.dataprovider.property.entity;

import com.booking.business.property.model.Property;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "property")
public class PropertyJpaEntity {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String hostName;
    @Column(nullable = false)
    private Set<String> amenities;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private LocalTime checkInTime;
    @Column(nullable = false)
    private LocalTime checkOutTime;
    @Column(nullable = false)
    private Float dailyRate;
    @Column(nullable = false)
    @CreationTimestamp
    private Instant createdAt;
    @Column(nullable = false)
    @UpdateTimestamp
    private Instant updatedAt;

    public PropertyJpaEntity() {}

    public PropertyJpaEntity(
            final UUID id,
            final String name,
            final String hostName,
            final String address,
            final Set<String> amenities,
            final LocalTime checkInTime,
            final LocalTime checkOutTime,
            final Float dailyRate) {
        this.id = id;
        this.name = name;
        this.hostName = hostName;
        this.address = address;
        this.amenities = amenities;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.dailyRate = dailyRate;
    }

    public PropertyJpaEntity(final UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHostName() {
        return hostName;
    }

    public Set<String> getAmenities() {
        return amenities;
    }

    public String getAddress() {
        return address;
    }

    public LocalTime getCheckInTime() {
        return checkInTime;
    }

    public LocalTime getCheckOutTime() {
        return checkOutTime;
    }

    public Float getDailyRate() {
        return dailyRate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
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
            property.name(),
            property.hostName(),
            property.address(),
            property.amenities(),
            property.checkInTime(),
            property.checkOutTime(),
            property.dailyRate()
        );
    }
}

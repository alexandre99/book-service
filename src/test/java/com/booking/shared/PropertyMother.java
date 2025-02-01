package com.booking.shared;

import com.booking.business.property.model.Property;
import com.booking.business.property.repository.PropertyRepository;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

@Component
public class PropertyMother {

    private final PropertyRepository repository;

    public static final LocalTime DEFAULT_CHECK_IN_TIME = LocalTime.of(14, 0);
    public static final LocalTime DEFAULT_CHECK_OUT_TIME = LocalTime.of(10, 0);
    public static final Float DEFAULT_DAILY_RATE = 200.0F;
    public static final String ELEVATOR = "Elevator";
    public static final String DEFAULT_ADDRESS = "New York, EUA";

    public PropertyMother(final PropertyRepository repository) {
        this.repository = repository;
    }

    public List<UUID> createProperties(final int quantity) {
        final List<UUID> ids = new ArrayList<>();
        IntStream.range(0, quantity).forEach(i -> {
            final var entity = new Property(
                    null,
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    DEFAULT_ADDRESS,
                    Set.of("Ar conditioning"),
                    DEFAULT_CHECK_IN_TIME,
                    DEFAULT_CHECK_OUT_TIME,
                    DEFAULT_DAILY_RATE
            );
            ids.add(repository.save(entity));
        });
        return ids;
    }

}

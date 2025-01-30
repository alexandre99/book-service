package com.booking.api.controller;

import com.booking.api.property.dto.PropertyRequestDTO;
import com.booking.business.property.model.Property;
import com.booking.business.property.repository.PropertyRepository;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PropertyControllerTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/property";
    private static final String LOCATION = "Location";
    private static final LocalTime DEFAULT_CHECK_IN_TIME = LocalTime.of(14, 0);
    private static final LocalTime DEFAULT_CHECK_OUT_TIME = LocalTime.of(10, 0);
    private static final Float DEFAULT_DAILY_RATE = 200.0F;
    public static final String ELEVATOR = "Elevator";

    @Autowired
    private PropertyRepository repository;

    @BeforeAll
    void setup() {
        super.mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldCreateProperty() throws Exception {
        //given
        final var hostName = UUID.randomUUID().toString();
        final var name = UUID.randomUUID().toString();
        final var requestBodyParsed = super.mapper.writeValueAsBytes(
                buildPropertyRequestDTO(name, hostName)
        );
        //when then
        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBodyParsed))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(LOCATION))
                .andExpect(header().string(LOCATION, matchesPattern("http://localhost/property/[a-f0-9\\-]{36}")));
    }

    @Test
    void shouldFindPropertyById() throws Exception {
        //given
        final var name = UUID.randomUUID().toString();
        final var hostName = UUID.randomUUID().toString();
        final var requestBodyParsed = super.mapper.writeValueAsBytes(
            buildPropertyRequestDTO(name, hostName)
        );

        //when then
        final var response = mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBodyParsed))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        final var uriToFetchSavedProperty = Objects.requireNonNull(
                response.getHeader(LOCATION)
        );

        final var formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        final var checkInTimeExpected = formatter.format(DEFAULT_CHECK_IN_TIME);
        final var checkOutTimeExpected = formatter.format(DEFAULT_CHECK_OUT_TIME);

        mockMvc.perform(get(uriToFetchSavedProperty))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.hostName").value(hostName))
                .andExpect(jsonPath("$.amenities").isArray())
                .andExpect(jsonPath("$.amenities[0]").value(ELEVATOR))
                .andExpect(jsonPath("$.checkInTime").value(checkInTimeExpected))
                .andExpect(jsonPath("$.checkOutTime").value(checkOutTimeExpected))
                .andExpect(jsonPath("$.dailyRate").value(DEFAULT_DAILY_RATE))
        ;
    }

    @Sql(statements = "DELETE FROM property")
    @Test
    void shouldFindAll() throws Exception {
        //given
        createProperties();
        //when then
        mockMvc.perform(get(BASE_URL.concat("?page=1&limit=2")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(4))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.content", hasSize(2)));

    }

    private void createProperties() {
        IntStream.range(0, 4).forEach(i -> {
            final var entity = new Property(
                null,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                Set.of("Ar conditioning"),
                DEFAULT_CHECK_IN_TIME,
                DEFAULT_CHECK_OUT_TIME,
                DEFAULT_DAILY_RATE
            );
            repository.save(entity);
        });
    }

    private PropertyRequestDTO buildPropertyRequestDTO(final String name, final String hostName) {
        return new PropertyRequestDTO(
            name,
            hostName,
            Set.of(ELEVATOR),
            DEFAULT_CHECK_IN_TIME,
            DEFAULT_CHECK_OUT_TIME,
            DEFAULT_DAILY_RATE
        );
    }

}

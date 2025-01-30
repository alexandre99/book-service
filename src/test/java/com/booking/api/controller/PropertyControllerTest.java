package com.booking.api.controller;

import com.booking.api.property.dto.PropertyRequestDTO;
import com.booking.business.property.model.Property;
import com.booking.business.property.repository.PropertyRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.*;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PropertyControllerTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/property";
    private static final String LOCATION = "Location";

    @Autowired
    private PropertyRepository repository;

    @Test
    void shouldCreateProperty() throws Exception {
        //given
        final var ownerId = UUID.randomUUID();
        final var name = UUID.randomUUID().toString();
        final var requestBodyParsed = super.mapper.writeValueAsBytes(
                buildPropertyRequestDTO(ownerId, name)
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
        final var ownerId = UUID.randomUUID();
        final var name = UUID.randomUUID().toString();
        final var requestBodyParsed = super.mapper.writeValueAsBytes(
                buildPropertyRequestDTO(ownerId, name)
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

        mockMvc.perform(get(uriToFetchSavedProperty))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.ownerId").value(ownerId.toString()))
                .andExpect(jsonPath("$.amenities").isArray())
                .andExpect(jsonPath("$.amenities[0]").value("Elevator"));
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

    @Sql(statements = "DELETE FROM property")
    @Test
    void shouldFindAllWhenThereIsNoPropertyRegistered() throws Exception {
        //when then
        mockMvc.perform(get(BASE_URL.concat("?page=1&limit=2")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.content").isEmpty());

    }

    @Test
    void shouldDeleteAndListOnlyEnabledProperty() throws Exception {
        //given
        final var propertyIds = createProperties();
        final var propertyIdsToDisable = propertyIds.subList(0, 2);
        //when
        propertyIdsToDisable.forEach(propertyId-> {
            try {
                mockMvc.perform(delete(BASE_URL.concat("/%s".formatted(propertyId))))
                        .andExpect(status().isNoContent());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        //then
        mockMvc.perform(get(BASE_URL.concat("?page=1&limit=2")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    private List<UUID> createProperties() {
        final List<UUID> propertyIds = new ArrayList<>();
        IntStream.range(0, 4).forEach(i -> {
            final var entity = new Property(
                null, UUID.randomUUID(), UUID.randomUUID().toString(), Set.of("Ar conditioning")
            );
            propertyIds.add(repository.save(entity));
        });
        return propertyIds;
    }

    private PropertyRequestDTO buildPropertyRequestDTO(final UUID ownerId, final String name) {
        return new PropertyRequestDTO(
                ownerId, name, Set.of("Elevator")
        );
    }

}

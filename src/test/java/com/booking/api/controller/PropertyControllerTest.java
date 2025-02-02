package com.booking.api.controller;

import com.booking.shared.PropertyMother;
import com.booking.api.property.dto.PropertyRequestDTO;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.booking.shared.PropertyMother.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class PropertyControllerTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/property";

    @Autowired
    private PropertyMother propertyMother;

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
        final var expectedCheckInTime = formatter.format(DEFAULT_CHECK_IN_TIME);
        final var expectedCheckOutTime = formatter.format(DEFAULT_CHECK_OUT_TIME);

        mockMvc.perform(get(uriToFetchSavedProperty))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.hostName").value(hostName))
                .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
                .andExpect(jsonPath("$.amenities").isArray())
                .andExpect(jsonPath("$.amenities[0]").value(ELEVATOR))
                .andExpect(jsonPath("$.checkInTime").value(expectedCheckInTime))
                .andExpect(jsonPath("$.checkOutTime").value(expectedCheckOutTime))
                .andExpect(jsonPath("$.dailyRate").value(DEFAULT_DAILY_RATE))
        ;
    }

    @Sql(statements = "DELETE FROM property")
    @Test
    void shouldFindAll() throws Exception {
        //given
        propertyMother.createProperties(4);
        //when then
        mockMvc.perform(get(BASE_URL.concat("?page=1&limit=2")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(4))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").exists())
                .andExpect(jsonPath("$.content[0].address").exists())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].dailyRate").exists());

    }

    private PropertyRequestDTO buildPropertyRequestDTO(final String name, final String hostName) {
        return new PropertyRequestDTO(
            name,
            hostName,
            DEFAULT_ADDRESS,
            Set.of(ELEVATOR),
            DEFAULT_CHECK_IN_TIME,
            DEFAULT_CHECK_OUT_TIME,
            DEFAULT_DAILY_RATE
        );
    }

}

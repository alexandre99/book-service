package com.booking.api.controller;

import com.booking.api.property.dto.BlockPropertyRequest;
import com.booking.business.booking.model.State;
import com.booking.dataprovider.property.repository.BlockPropertyJpaEntityRepository;
import com.booking.shared.BookingMother;
import com.booking.shared.PropertyMother;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class BlockPropertyControllerTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/block-property";

    @Autowired
    private BlockPropertyJpaEntityRepository entityRepository;

    @Autowired
    private PropertyMother propertyMother;

    @Autowired
    private BookingMother bookingMother;

    @Test
    void shouldSaveBlockProperty() throws Exception {
        //given
        final var propertyIds = propertyMother.createProperties(1);
        final var blockPropertyRequest = new BlockPropertyRequest(
            propertyIds.get(0),
            LocalDate.now(),
            LocalDate.now().plusDays(1)
        );
        final var content = mapper.writeValueAsString(blockPropertyRequest);

        //when then
        mockMvc.perform(post(BASE_URL)
                    .contentType("application/json")
                    .content(content))
                .andExpect(status().isCreated())
                .andExpect(header().exists(LOCATION))
                .andExpect(header().string(LOCATION, matchesPattern("http://localhost/block-property/[a-f0-9\\-]{36}")));
    }

    @Test
    void shouldUpdateBlockProperty() throws Exception {
        //given
        final var propertyIds = propertyMother.createProperties(1);
        final var blockPropertyRequest = new BlockPropertyRequest(
                propertyIds.get(0),
                LocalDate.now(),
                LocalDate.now().plusDays(1)
        );
        final var content = mapper.writeValueAsString(blockPropertyRequest);

        final var response = mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(content))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();


        final var location = response.getHeader(LOCATION);
        assert location != null;
        final var blockPropertyId = location.substring(location.lastIndexOf("/") + 1);

        //when
        final var updateBlockPropertyRequest = new BlockPropertyRequest(
            propertyIds.get(0),
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(2)
        );
        final var updateContent = mapper.writeValueAsString(updateBlockPropertyRequest);
        mockMvc.perform(put(BASE_URL.concat("/%s".formatted(blockPropertyId)))
                        .contentType("application/json")
                        .content(updateContent))
                .andDo(print())
                .andExpect(status().isNoContent());

        //then
        final var updatedBlockProperty = entityRepository.findBlockPropertyById(
                                                        UUID.fromString(blockPropertyId)
                                                    ).orElseThrow();

        assertThat(updatedBlockProperty.getStartDate())
                .isEqualTo(updateBlockPropertyRequest.startDate());
        assertThat(updatedBlockProperty.getEndDate())
                .isEqualTo(updateBlockPropertyRequest.endDate());
        assertThat(updatedBlockProperty.getProperty().getId())
                .isEqualTo(updateBlockPropertyRequest.propertyId());
    }

    @Test
    void shouldFindBlockPropertyById() throws Exception {
        //given
        final var propertyId = propertyMother.createProperties(1).get(0);
        final var blockPropertyRequest = new BlockPropertyRequest(
                propertyId,
                LocalDate.now(),
                LocalDate.now().plusDays(1)
        );
        final var content = mapper.writeValueAsString(blockPropertyRequest);

        final var response = mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(content))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final var location = response.getHeader(LOCATION);
        assert location != null;
        final var blockPropertyId = location.substring(location.lastIndexOf("/") + 1);

        //when

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(blockPropertyId))
                .andExpect(jsonPath("$.propertyId").value(propertyId.toString()))
                .andExpect(jsonPath("$.startDate").value(blockPropertyRequest.startDate().toString()))
                .andExpect(jsonPath("$.endDate").value(blockPropertyRequest.endDate().toString()));
    }

    @Sql(statements = "DELETE FROM block_property")
    @Test
    void shouldFindAll() throws Exception {
        //given
        final var propertyIds = propertyMother.createProperties(2);
        propertyIds.forEach(propertyId -> {
            try {
                final var blockPropertyRequest = new BlockPropertyRequest(
                        propertyId,
                        LocalDate.now(),
                        LocalDate.now().plusDays(1)
                );

                final var content = mapper.writeValueAsString(blockPropertyRequest);


                mockMvc.perform(post(BASE_URL)
                                .contentType("application/json")
                                .content(content))
                        .andDo(print())
                        .andExpect(status().isCreated());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        //when then
        mockMvc.perform(get(BASE_URL.concat("?page=1&limit=1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void shouldDeleteBlockProperty() throws Exception {
        //given
        final var propertyId = propertyMother.createProperties(1).get(0);
        final var blockPropertyRequest = new BlockPropertyRequest(
                propertyId,
                LocalDate.now(),
                LocalDate.now().plusDays(1)
        );
        final var content = mapper.writeValueAsString(blockPropertyRequest);

        final var response = mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(content))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final var location = response.getHeader(LOCATION);
        assert location != null;
        final var blockPropertyId = location.substring(location.lastIndexOf("/") + 1);

        //when
        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent());

        //then
        assertThat(this.entityRepository.findBlockPropertyById(UUID.fromString(blockPropertyId))
                .isEmpty()).isTrue();
    }

    @Test
    void shouldNotBlockWhenThereIsOverlap() throws Exception {
        //given
        final var propertyIds = propertyMother.createProperties(1);
        final var blockPropertyRequest = new BlockPropertyRequest(
                propertyIds.get(0),
                LocalDate.now(),
                LocalDate.now().plusDays(1)
        );
        final var content = mapper.writeValueAsString(blockPropertyRequest);

        //when
        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(content))
                .andExpect(status().isCreated());

        //then
        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(content))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldNotBlockWhenThereIsOverlapWithBooking() throws Exception {
        //given
        final var propertyId = this.propertyMother.createProperties(1).get(0);
        final var bookingEntity = bookingMother.createBookingEntity(propertyId, State.ACTIVE);

        final var blockPropertyRequest = new BlockPropertyRequest(
                propertyId,
                LocalDate.now(),
                LocalDate.now().plusDays(1)
        );
        final var content = mapper.writeValueAsString(blockPropertyRequest);

        //then when
        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(content))
                .andExpect(status().isConflict());
    }

}

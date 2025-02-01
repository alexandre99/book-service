package com.booking.api.controller;

import com.booking.shared.PropertyMother;
import com.booking.api.booking.dto.BookingRequestDTO;
import com.booking.api.booking.dto.GuestDetailsDTO;
import com.booking.api.booking.dto.UpdateBookingDatesRequest;
import com.booking.business.booking.model.State;
import com.booking.dataprovider.booking.entity.BookingJpaEntity;
import com.booking.dataprovider.booking.model.GuestDetails;
import com.booking.dataprovider.booking.repository.BookingJpaEntityRepository;
import com.booking.dataprovider.property.entity.PropertyJpaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookingControllerTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/booking";

    @Autowired
    private PropertyMother propertyMother;
    @Autowired
    private BookingJpaEntityRepository repository;

    @Test
    void shouldCreateBooking() throws Exception {
        //given
        final var propertyIds = this.propertyMother.createProperties(1);

        final var guestDetails = new GuestDetailsDTO(
                "Joe Doe",
                "joedoe@gmai.com",
                "+5591232265896",
                1,
                0,
                0,
                "specialRequests"
        );
        final var bookingRequest = new BookingRequestDTO(
                propertyIds.get(0),
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                guestDetails
        );

        final var content = mapper.writeValueAsString(bookingRequest);

        //when then
        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(content))
                .andExpect(status().isCreated())
                .andExpect(header().exists(LOCATION))
                .andExpect(header().string(LOCATION, matchesPattern("http://localhost/booking/[a-f0-9\\-]{36}")));
    }

    @Test
    void shouldFindBookingById() throws Exception {
        //given
        final var propertyId = this.propertyMother.createProperties(1)
                .get(0);

        final var guestDetails = new GuestDetailsDTO(
                "Joe Doe",
                "joedoe@gmai.com",
                "+5591232265896",
                1,
                0,
                0,
                "specialRequests"
        );
        final var bookingRequest = new BookingRequestDTO(
                propertyId,
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                guestDetails
        );

        final var content = mapper.writeValueAsString(bookingRequest);

        //when
        final var response = mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(content))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        final var uriToFetchSavedBooking = Objects.requireNonNull(
                response.getHeader(LOCATION)
        );

        //then
        mockMvc.perform(get(uriToFetchSavedBooking))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.startDate").value(bookingRequest.startDate().toString()))
                .andExpect(jsonPath("$.endDate").value(bookingRequest.endDate().toString()))
                .andExpect(jsonPath("$.state").value(State.ACTIVE.toString()))
                .andExpect(jsonPath("$.propertyDetails.id").value(propertyId.toString()))
                .andExpect(jsonPath("$.guestDetails.fullName").value(guestDetails.fullName()))
                .andExpect(jsonPath("$.guestDetails.email").value(guestDetails.email()))
                .andExpect(jsonPath("$.guestDetails.phone").value(guestDetails.phone()))
                .andExpect(jsonPath("$.guestDetails.numberOfAdults").value(guestDetails.numberOfAdults()))
                .andExpect(jsonPath("$.guestDetails.numberOfChildren").value(guestDetails.numberOfChildren()))
                .andExpect(jsonPath("$.guestDetails.numberOfInfants").value(guestDetails.numberOfInfants()))
                .andExpect(jsonPath("$.guestDetails.specialRequests").value(guestDetails.specialRequests()));
    }
    
    @Test
    void shouldCancelBooking() throws Exception {
        //given
        final var propertyIds = this.propertyMother.createProperties(1);
        final var entity = buildEntity(propertyIds.get(0), State.ACTIVE);
        repository.save(entity);

        //when
        mockMvc.perform(patch(BASE_URL.concat("/%s/cancel").formatted(entity.getId())))
                .andExpect(status().isNoContent());

        //then
        final var bookingFound = this.repository.findById(entity.getId()).orElseThrow();
        assertThat(bookingFound.getState()).isEqualTo(State.CANCELED);
    }

    @Test
    void shouldRebookABooking() throws Exception {
        //given
        final var propertyIds = this.propertyMother.createProperties(1);
        final var entity = buildEntity(propertyIds.get(0), State.CANCELED);
        repository.save(entity);

        //when
        mockMvc.perform(patch(BASE_URL.concat("/%s/rebook").formatted(entity.getId())))
                .andExpect(status().isNoContent());

        //then
        final var bookingFound = this.repository.findById(entity.getId()).orElseThrow();
        assertThat(bookingFound.getState()).isEqualTo(State.ACTIVE);
    }

    @Test
    void shouldDeleteBooking() throws Exception {
        //given
        final var propertyIds = this.propertyMother.createProperties(2);
        final var canceledId = propertyIds.get(0);
        final var activeId = propertyIds.get(1);

        final var canceledEntity = buildEntity(canceledId, State.CANCELED);
        repository.save(canceledEntity);

        final var activeEntity = buildEntity(activeId, State.ACTIVE);
        repository.save(activeEntity);

        //when
        mockMvc.perform(delete(BASE_URL.concat("/%s").formatted(canceledEntity.getId())))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete(BASE_URL.concat("/%s").formatted(activeEntity.getId())))
                .andExpect(status().isNoContent());

        //then
        final var reservations = repository.findAllById(Set.of(canceledEntity.getId(), activeEntity.getId()));
        assertThat(
            reservations.stream().allMatch(r -> r.getState().equals(State.DELETED))
        ).isTrue();
    }

    @Test
    void shouldUpdateBookingDates() throws Exception {
        //given
        final var propertyIds = this.propertyMother.createProperties(1);
        final var entity = buildEntity(propertyIds.get(0), State.ACTIVE);
        repository.save(entity);

        final var updateBookingDatesRequest = new UpdateBookingDatesRequest(
            LocalDate.now().plusDays(2),
            LocalDate.now().plusDays(4)
        );

        final var content = mapper.writeValueAsString(updateBookingDatesRequest);

        //when
        mockMvc.perform(patch(BASE_URL.concat("/%s/update-booking-dates").formatted(entity.getId()))
                        .contentType("application/json")
                        .content(content))
                .andExpect(status().isNoContent());

        //then
        final var bookingFound = this.repository.findById(entity.getId()).orElseThrow();
        assertThat(bookingFound.getStartDate()).isEqualTo(updateBookingDatesRequest.startDate());
        assertThat(bookingFound.getEndDate()).isEqualTo(updateBookingDatesRequest.endDate());
    }

    @Test
    void shouldUpdateGuestDetails() throws Exception {
        //given
        final var propertyIds = this.propertyMother.createProperties(1);
        final var entity = buildEntity(propertyIds.get(0), State.ACTIVE);
        repository.save(entity);

        final var guestDetailsDTO = new GuestDetailsDTO(
            "Joe Doe 2",
            "joedoe2@gmai.com",
            "+5511911111111",
            2,
            1,
            1,
            "new specialRequests"
        );

        final var content = mapper.writeValueAsString(guestDetailsDTO);

        //when
        mockMvc.perform(patch(BASE_URL.concat("/%s/update-guest-details").formatted(entity.getId()))
                        .contentType("application/json")
                        .content(content))
                .andExpect(status().isNoContent());

        //then
        final var bookingFound = this.repository.findById(entity.getId()).orElseThrow();
        final var updatedGuestDetails = bookingFound.getGuestDetails();
        assertThat(updatedGuestDetails.fullName()).isEqualTo(guestDetailsDTO.fullName());
        assertThat(updatedGuestDetails.email()).isEqualTo(guestDetailsDTO.email());
        assertThat(updatedGuestDetails.phone()).isEqualTo(guestDetailsDTO.phone());
        assertThat(updatedGuestDetails.numberOfAdults()).isEqualTo(guestDetailsDTO.numberOfAdults());
        assertThat(updatedGuestDetails.numberOfChildren()).isEqualTo(guestDetailsDTO.numberOfChildren());
        assertThat(updatedGuestDetails.numberOfInfants()).isEqualTo(guestDetailsDTO.numberOfInfants());
        assertThat(updatedGuestDetails.specialRequests()).isEqualTo(guestDetailsDTO.specialRequests());
    }

    private BookingJpaEntity buildEntity(final UUID propertyId,
                                         final State state) {
        return new BookingJpaEntity(
            null,
            new PropertyJpaEntity(propertyId),
            new GuestDetails(
                "Joe Doe",
                "joedoe@gmai.com",
                "+5591232265896",
                1,
                0,
                0,
                "specialRequests"
            ),
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            state
        );
    }

}

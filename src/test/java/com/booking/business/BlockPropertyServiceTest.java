package com.booking.business;

import com.booking.business.booking.service.BookingService;
import com.booking.business.property.model.BlockProperty;
import com.booking.business.property.service.BlockPropertyRepository;
import com.booking.business.property.service.impl.BlockPropertyServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static com.booking.business.property.service.impl.BlockPropertyServiceImpl.PROPERTY_BLOCK_FAILED_THERE_IS_A_BOOKING_IN_THESE_DATES;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockPropertyServiceTest {

    @Mock
    private BlockPropertyRepository repository;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BlockPropertyServiceImpl service;

    @Test
    void shouldSaveBlock() {
        //given
        final var block = buildBLock(
            LocalDate.now(), LocalDate.now().plusDays(1)
        );

        //when
        this.service.save(block);

        //then
        verify(this.bookingService).validateOverLap(
            block.propertyId(), block.startDate(), block.endDate(), PROPERTY_BLOCK_FAILED_THERE_IS_A_BOOKING_IN_THESE_DATES
        );
        verify(this.bookingService).validateOverLap(
            any(), any(), any(), anyString()
        );
        verify(this.repository).hasOverLap(
            block.propertyId(), block.startDate(), block.endDate()
        );
        verify(this.repository).hasOverLap(
            any(), any(), any()
        );
        verify(this.repository).save(block);
        verify(this.repository).save(any());
    }

    @Test
    void shouldNotSaveBlockWhenStarDateIsEqualsToEndDate() {
        //given
        final var block = buildBLock(LocalDate.now(), LocalDate.now());

        //when then
        verifyWhenStartDateIsInvalid(block);
    }

    @Test
    void shouldNotSaveBLockWhenStartDateIsBeforeEndDate() {
        //given
        final var block = buildBLock(LocalDate.now().plusDays(1), LocalDate.now());

        verifyWhenStartDateIsInvalid(block);
    }

    @Test
    void shouldNotSaveBlockWhenThereIsAnOverLapWithBooking() {
        //given
        final var block = buildBLock(
            LocalDate.now(), LocalDate.now().plusDays(1)
        );

        doThrow(IllegalArgumentException.class).when(this.bookingService).validateOverLap(
            block.propertyId(), block.startDate(), block.endDate(), PROPERTY_BLOCK_FAILED_THERE_IS_A_BOOKING_IN_THESE_DATES
        );

        //when then
        assertThatIllegalArgumentException().isThrownBy(
            () -> this.service.save(block)
        );

        //then
        verify(this.bookingService).validateOverLap(
            block.propertyId(), block.startDate(), block.endDate(), PROPERTY_BLOCK_FAILED_THERE_IS_A_BOOKING_IN_THESE_DATES
        );
        verify(this.bookingService).validateOverLap(
            any(), any(), any(), anyString()
        );
        verify(this.repository, never()).hasOverLap(
            any(), any(), any()
        );
        verify(this.repository, never()).save(any());
    }

    @Test
    void shouldNotSaveBlockWhenThereIsAnOverLapWithAnotherBlock() {
        //given
        final var block = buildBLock(
            LocalDate.now(), LocalDate.now().plusDays(1)
        );
        when(this.repository.hasOverLap(
            block.propertyId(), block.startDate(), block.endDate()
        )).thenReturn(true);

        //when then
        assertThatIllegalStateException().isThrownBy(
            () -> this.service.save(block)
        );

        //then
        verify(this.bookingService).validateOverLap(
                block.propertyId(), block.startDate(), block.endDate(), PROPERTY_BLOCK_FAILED_THERE_IS_A_BOOKING_IN_THESE_DATES
        );
        verify(this.bookingService).validateOverLap(
                any(), any(), any(), anyString()
        );
        verify(this.repository).hasOverLap(
                block.propertyId(), block.startDate(), block.endDate()
        );
        verify(this.repository).hasOverLap(
                any(), any(), any()
        );
        verify(this.repository, never()).save(any());
    }

    @Test
    void shouldDelete() {
        //given
        final var blockId = UUID.randomUUID();
        when(this.repository.existsById(blockId))
                .thenReturn(true);
        //when
        this.service.deleteById(blockId);
        //then
        verify(this.repository).existsById(blockId);
        verify(this.repository).existsById(any());

        verify(this.repository).deleteById(blockId);
        verify(this.repository).deleteById(any());
    }

    @Test
    void shouldNotDeleteWhenBlockIdIsInvalid() {
        //given
        final var blockId = UUID.randomUUID();
        when(this.repository.existsById(blockId))
            .thenReturn(false);
        //when then
        assertThatIllegalArgumentException().isThrownBy(
            () -> this.service.deleteById(blockId)
        );

        verify(this.repository).existsById(blockId);
        verify(this.repository).existsById(any());

        verify(this.repository, never()).deleteById(blockId);
        verify(this.repository, never()).deleteById(any());
    }

    private void verifyWhenStartDateIsInvalid(BlockProperty block) {
        //when then
        assertThatIllegalArgumentException().isThrownBy(
                () -> this.service.save(block)
        );
        verify(this.bookingService, never()).validateOverLap(
                any(), any(), any(), anyString()
        );
        verify(this.repository, never()).hasOverLap(
                any(), any(), any()
        );
        verify(this.repository, never()).save(any());
    }

    private BlockProperty buildBLock(final LocalDate startDate,
                                     final LocalDate endDate) {
        return new BlockProperty(
          null,
          UUID.randomUUID(),
          startDate,
          endDate
        );
    }

}

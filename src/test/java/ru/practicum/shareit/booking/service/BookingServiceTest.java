package ru.practicum.shareit.booking.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.BookingBaseTest;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOutAbs;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.OffsetLimitPageable;
import ru.practicum.shareit.util.exception.ForbiddenException;
import ru.practicum.shareit.util.exception.ItemIsNotAvailableException;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.StatusChangeException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.model.BookingStatus.*;
import static ru.practicum.shareit.booking.service.BookingServiceImpl.getLastBooking;
import static ru.practicum.shareit.booking.service.BookingServiceImpl.getNextBooking;

@SpringBootTest
public class BookingServiceTest extends BookingBaseTest {

    @MockBean
    BookingRepository bookingRepository;

    @MockBean
    ItemService itemService;

    @MockBean
    UserService userService;

    @MockBean
    BookingMapper bookingMapper;

    @Autowired
    BookingService bookingService;

    @BeforeEach
    protected void setUp() {
        super.setUp();

        doNothing().when(userService).existenceCheck(anyInt());

        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDtoOut);
        when(bookingMapper.toDto(anyList())).thenReturn(List.of(bookingDtoOut));
        when(bookingMapper.toShortDto(any(Booking.class))).thenReturn(shortBookingDtoOut);
        when(bookingMapper.fromDto(any(BookingDtoIn.class), anyInt())).thenReturn(booking);
    }

    @Test
    void create_shouldInvokeRepositoryAndReturnTheSame() {
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDtoOutAbs savedBooking = bookingService.create(bookingDtoIn, booker.getId());

        verify(bookingRepository, times(1)).save(booking);
        assertThat(savedBooking).isEqualTo(bookingDtoOut);
    }

    @Test
    void create_shouldThrowItemIsNotAvailableExceptionWhenItemIsNotAvailable() {
        item.setAvailable(false);

        assertThatThrownBy(() -> {
            bookingService.create(bookingDtoIn, 1);
        }).isInstanceOf(ItemIsNotAvailableException.class);
    }

    @Test
    void create_shouldThrowNotFoundExceptionWhenOwnerAndBookerAreTheSame() {
        booking.setBooker(item.getOwner());

        assertThatThrownBy(() -> {
            bookingService.create(bookingDtoIn, 1);
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getById_shouldThrowNotFoundExceptionWhenRepositoryReturnsEmpty() {
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> {
            bookingService.getById(1, 1);
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getById_shouldThrowForbiddenExceptionWhenUserIsNotOwnerOrBooker() {
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));

        final int USER_ID = 3;

        assertThat(USER_ID)
                .isNotEqualTo(booking.getItem().getOwner().getId())
                .isNotEqualTo(booking.getBooker().getId());

        assertThatThrownBy(() -> {
            bookingService.getById(1, USER_ID);
        }).isInstanceOf(ForbiddenException.class);
    }

    @Test
    void getById_shouldInvokeRepositoryAndReturnTheSameWhenUserIsOwner() {
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));

        BookingDtoOutAbs bookingById = bookingService.getById(1, booking.getItem().getOwner().getId());

        assertThat(bookingById).isEqualTo(bookingDtoOut);

        verify(bookingRepository, times(1)).findById(1);
    }

    @Test
    void getById_shouldInvokeRepositoryAndReturnTheSameWhenUserIsBooker() {
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));

        BookingDtoOutAbs bookingById = bookingService.getById(1, booking.getBooker().getId());

        verify(bookingRepository, times(1)).findById(1);
        assertThat(bookingById).isEqualTo(bookingDtoOut);
    }

    @Test
    void getAllByBooker_shouldInvokeRepositoryAndReturnTheSame() {
        when(bookingRepository.getBookingByBooker(anyInt(), any(OffsetLimitPageable.class)))
                .thenReturn(Arrays.asList(booking));

        List<? extends BookingDtoOutAbs> allByBooker = bookingService.getAllByBooker(1, 0, 10);

        assertThat(allByBooker.get(0)).isEqualTo(bookingDtoOut);

        verify(bookingRepository, times(1)).getBookingByBooker(eq(1), any(OffsetLimitPageable.class));
    }

    @Test
    void getBookingByBookerAndStatus_shouldInvokeRepositoryAndReturnTheSame() {

        when(bookingRepository.getBookingByBookerAndStatus(anyInt(), any(BookingStatus.class), any(OffsetLimitPageable.class)))
                .thenReturn(Arrays.asList(booking));

        List<? extends BookingDtoOutAbs> bookings = bookingService.getBookingByBookerAndStatus(1, WAITING, 0, 10);

        assertThat(bookings.get(0))
                .isEqualTo(bookingDtoOut);

        verify(bookingRepository, times(1)).getBookingByBookerAndStatus(eq(1), eq(WAITING), any(OffsetLimitPageable.class));
    }

    @Test
    void getPastBookingByBooker_shouldInvokeRepositoryAndReturnTheSame() {
        when(bookingRepository.getPastBookingByBooker(anyInt(), any(OffsetLimitPageable.class)))
                .thenReturn(Arrays.asList(booking));

        List<? extends BookingDtoOutAbs> bookings = bookingService.getPastBookingByBooker(1, 0, 10);

        verify(bookingRepository, times(1)).getPastBookingByBooker(eq(1), any(OffsetLimitPageable.class));
        assertThat(bookings.get(0)).isEqualTo(bookingDtoOut);
    }

    @Test
    void getCurrentBookingByBooker_shouldInvokeRepositoryAndReturnTheSame() {
        when(bookingRepository.getCurrentBookingByBooker(anyInt(), any(OffsetLimitPageable.class)))
                .thenReturn(Arrays.asList(booking));

        List<? extends BookingDtoOutAbs> bookings = bookingService.getCurrentBookingByBooker(1, 0, 10);

        verify(bookingRepository, times(1)).getCurrentBookingByBooker(eq(1), any(OffsetLimitPageable.class));
        assertThat(bookings.get(0)).isEqualTo(bookingDtoOut);
    }

    @Test
    void getFutureBookingByBooker_shouldInvokeRepositoryAndReturnTheSame() {
        when(bookingRepository.getFutureBookingByBooker(anyInt(), any(OffsetLimitPageable.class)))
                .thenReturn(Arrays.asList(booking));

        List<? extends BookingDtoOutAbs> bookings = bookingService.getFutureBookingByBooker(1, 0, 10);

        verify(bookingRepository, times(1)).getFutureBookingByBooker(eq(1), any(OffsetLimitPageable.class));
        assertThat(bookings.get(0)).isEqualTo(bookingDtoOut);
    }

    @Test
    void getAllByOwner_shouldInvokeRepositoryAndReturnTheSame() {
        when(bookingRepository.getBookingByOwner(anyInt(), any(OffsetLimitPageable.class)))
                .thenReturn(Arrays.asList(booking));

        List<? extends BookingDtoOutAbs> bookings = bookingService.getAllByOwner(1, 0, 10);

        verify(bookingRepository, times(1)).getBookingByOwner(eq(1), any(OffsetLimitPageable.class));
        assertThat(bookings.get(0)).isEqualTo(bookingDtoOut);
    }

    @Test
    void getBookingByOwnerAndStatus_shouldInvokeRepositoryAndReturnTheSame() {
        when(bookingRepository.getBookingByOwnerAndStatus(anyInt(), any(BookingStatus.class), any(OffsetLimitPageable.class)))
                .thenReturn(Arrays.asList(booking));

        List<? extends BookingDtoOutAbs> bookings = bookingService.getBookingByOwnerAndStatus(1, WAITING, 0, 10);

        verify(bookingRepository, times(1)).getBookingByOwnerAndStatus(eq(1), eq(WAITING), any(OffsetLimitPageable.class));
        assertThat(bookings.get(0)).isEqualTo(bookingDtoOut);
    }

    @Test
    void getPastBookingByOwner_shouldInvokeRepositoryAndReturnTheSame() {
        when(bookingRepository.getPastBookingByOwner(anyInt(), any(OffsetLimitPageable.class)))
                .thenReturn(Arrays.asList(booking));

        List<? extends BookingDtoOutAbs> bookings = bookingService.getPastBookingByOwner(1, 0, 10);

        verify(bookingRepository, times(1)).getPastBookingByOwner(eq(1), any(OffsetLimitPageable.class));
        assertThat(bookings.get(0)).isEqualTo(bookingDtoOut);
    }

    @Test
    void getCurrentBookingByOwner_shouldInvokeRepositoryAndReturnTheSame() {
        when(bookingRepository.getCurrentBookingByOwner(anyInt(), any(OffsetLimitPageable.class)))
                .thenReturn(Arrays.asList(booking));

        List<? extends BookingDtoOutAbs> bookings = bookingService.getCurrentBookingByOwner(1, 0, 10);

        verify(bookingRepository, times(1)).getCurrentBookingByOwner(eq(1), any(OffsetLimitPageable.class));
        assertThat(bookings.get(0)).isEqualTo(bookingDtoOut);
    }

    @Test
    void getFutureBookingByOwner_shouldInvokeRepositoryAndReturnTheSame() {
        when(bookingRepository.getFutureBookingByOwner(anyInt(), any(OffsetLimitPageable.class)))
                .thenReturn(Arrays.asList(booking));

        List<? extends BookingDtoOutAbs> bookings = bookingService.getFutureBookingByOwner(1, 0, 10);

        verify(bookingRepository, times(1)).getFutureBookingByOwner(eq(1), any(OffsetLimitPageable.class));
        assertThat(bookings.get(0)).isEqualTo(bookingDtoOut);
    }

    @Test
    void approve_shouldThrowForbiddenExceptionWhenUserIsNotOwner() {
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));

        final int OTHER_ID = 3;
        assertThat(OTHER_ID)
                .isNotEqualTo(booking.getItem().getOwner().getId());

        assertThatThrownBy(() -> {
            bookingService.approve(1, OTHER_ID, true);
        }).isInstanceOf(ForbiddenException.class);
    }

    @Test
    void approve_shouldThrowForbiddenExceptionWhenStatusInNotWaiting() {
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));

        booking.setStatus(CANCELED);

        assertThatThrownBy(() -> {
            bookingService.approve(1, booking.getItem().getOwner().getId(), true);
        }).isInstanceOf(StatusChangeException.class);
    }

    @Test
    void approve_shouldThrowForbiddenExceptionWhenStatusIsWaiting() {
        booking.setStatus(WAITING);

        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));

        bookingService.approve(1, booking.getItem().getOwner().getId(), true);
    }

    @Test
    void delete_shouldInvokeRepositoryDelete() {
        when(bookingRepository.existsById(anyInt())).thenReturn(true);

        doNothing().when(bookingRepository)
                .deleteById(anyInt());

        bookingService.delete(1);
        verify(bookingRepository, times(1)).deleteById(1);
    }

    @Test
    void delete_shouldThrowNotFoundExceptionWhenInvokeRepositoryWithWrongId() {
        when(bookingRepository.existsById(anyInt())).thenReturn(false);

        assertThatThrownBy(() -> bookingService.delete(1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Booking with ID 1 is not found");
    }

    @Test
    void getLastBooking_shouldReturnLastBooking() {
        LocalDateTime now = LocalDateTime.now();

        Booking beforeOneLastBooking = Booking.builder()
                .startDate(now.minusMinutes(3))
                .endDate(now.minusMinutes(2))
                .item(item)
                .booker(booking.getBooker())
                .status(APPROVED)
                .build();

        Booking lastBooking = Booking.builder()
                .startDate(now.minusMinutes(2))
                .endDate(now.minusMinutes(1))
                .item(item)
                .booker(booking.getBooker())
                .status(APPROVED)
                .build();

        Booking currentBooking = Booking.builder()
                .startDate(now.minusMinutes(1))
                .endDate(now.plusMinutes(1))
                .item(item)
                .booker(booking.getBooker())
                .status(APPROVED)
                .build();
        item.setBookings(Arrays.asList(currentBooking, lastBooking, beforeOneLastBooking));

        assertThat(getLastBooking(item, LocalDateTime.now())).isEqualTo(lastBooking);
    }

    @Test
    void getNextBooking_shouldReturnNextBooking() {
        LocalDateTime now = LocalDateTime.now();

        Booking afterOneNextBooking = Booking.builder()
                .startDate(now.plusMinutes(2))
                .endDate(now.plusMinutes(3))
                .item(item)
                .booker(booking.getBooker())
                .status(APPROVED)
                .build();

        Booking nextBooking = Booking.builder()
                .startDate(now.plusMinutes(1))
                .endDate(now.plusMinutes(2))
                .item(item)
                .booker(booking.getBooker())
                .status(APPROVED)
                .build();

        Booking currentBooking = Booking.builder()
                .startDate(now.minusMinutes(1))
                .endDate(now.plusMinutes(1))
                .item(item)
                .booker(booking.getBooker())
                .status(APPROVED)
                .build();
        item.setBookings(Arrays.asList(currentBooking, nextBooking, afterOneNextBooking));

        assertThat(getNextBooking(item, LocalDateTime.now())).isEqualTo(nextBooking);
    }

}

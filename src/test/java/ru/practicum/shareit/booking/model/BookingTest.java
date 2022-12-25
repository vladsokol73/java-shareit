package ru.practicum.shareit.booking.model;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingBaseTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.booking.model.BookingStatus.*;

public class BookingTest extends BookingBaseTest {

    @Test
    void equalsTest() {
        Booking x = Booking.builder()
                .startDate(now.minusDays(2))
                .endDate(now.minusDays(1))
                .item(item)
                .booker(booker)
                .build();

        Booking y = Booking.builder()
                .startDate(now.minusDays(2))
                .endDate(now.minusDays(1))
                .item(item)
                .booker(booker)
                .build();

        assertThat(x.equals(y) && y.equals(x)).isTrue();
        assertThat(x.hashCode() == y.hashCode()).isTrue();
    }

    @Test
    void requiredArgsConstructorTest() {
        LocalDateTime start = now.minusDays(2);
        LocalDateTime end = now.minusDays(1);

        Booking booking = new Booking(start, end, item, booker);

        assertThat(booking.getStartDate()).isEqualTo(start);
        assertThat(booking.getEndDate()).isEqualTo(end);
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getBooker()).isEqualTo(booker);
    }

    @Test
    void noArgsConstructorTest() {
        LocalDateTime start = now.minusDays(2);
        LocalDateTime end = now.minusDays(1);
        BookingStatus status = APPROVED;

        Booking booking = new Booking();
        booking.setStartDate(start);
        booking.setEndDate(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);

        assertThat(booking.getStartDate()).isEqualTo(start);
        assertThat(booking.getEndDate()).isEqualTo(end);
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getBooker()).isEqualTo(booker);
        assertThat(booking.getStatus()).isEqualTo(status);
    }

    @Test
    void toStringTest() {
        Booking booking = Booking.builder()
                .startDate(now.minusDays(2))
                .endDate(now.minusDays(1))
                .item(item)
                .booker(booker)
                .build();

        assertThat(booking.toString()).startsWith(booking.getClass().getSimpleName());
    }

    @Test
    void bookingStatusTest() {
        assertThat(WAITING.getTitle()).isEqualTo("новое бронирование, ожидает одобрения");
        assertThat(APPROVED.getTitle()).isEqualTo("бронирование подтверждено владельцем");
        assertThat(REJECTED.getTitle()).isEqualTo("бронирование отклонено владельцем");
        assertThat(CANCELED.getTitle()).isEqualTo("бронирование отменено создателем");
    }
}
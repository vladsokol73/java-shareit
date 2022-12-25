package ru.practicum.shareit.booking.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.BookingBaseTest;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.service.ItemFactory;
import ru.practicum.shareit.user.service.UserFactory;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BookingMapperTest extends BookingBaseTest {

    @Autowired
    BookingMapper mapper;

    @MockBean
    ItemFactory itemFactory;

    @MockBean
    UserFactory userFactory;

    @Test
    void toDto_shouldInvokeServiceAndReturnTheSame() {
        assertThat(mapper.toDto(booking)).isEqualTo(bookingDtoOut);
    }

    @Test
    void toDto_shouldInvokeServiceAndReturnNull() {
        Booking booking = null;
        assertThat(mapper.toDto(booking)).isNull();
    }


    @Test
    void toDtoList_shouldInvokeServiceAndReturnTheSame() {
        assertThat(mapper.toDto(List.of(booking))).isEqualTo(List.of(bookingDtoOut));
    }

    @Test
    void toDtoList_shouldInvokeServiceAndReturnNull() {
        List<Booking> booking = null;
        assertThat(mapper.toDto(booking)).isNull();
    }

    @Test
    void toShortDto_shouldInvokeServiceAndReturnTheSame() {
        assertThat(mapper.toShortDto(booking)).isEqualTo(shortBookingDtoOut);
    }

    @Test
    void toShortDto_shouldInvokeServiceAndReturnTheSame_shouldInvokeServiceAndReturnNull() {
        Booking booking = null;
        assertThat(mapper.toShortDto(booking)).isNull();
    }

    @Test
    void fromDto_shouldInvokeServiceAndReturnTheSame() {
        when(itemFactory.get(anyInt())).thenReturn(item);

        when(userFactory.get(anyInt())).thenReturn(booking.getBooker());
        booking.setStatus(null);
        assertThat(mapper.fromDto(bookingDtoIn, 1)).isEqualTo(booking);
    }

   @Test
    void fromDto_shouldInvokeServiceAndReturnNull() {
        BookingDtoIn bookingDtoIn = null;
        assertThat(mapper.fromDto(bookingDtoIn, null)).isNull();
    }
}
package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.ShortBookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

public class BookingBaseTest {

    protected User owner, booker;
    protected UserDtoOut bookerDtoOut;

    protected Item item;
    protected ItemDtoOut itemDtoOut;

    protected Booking booking;
    protected BookingDtoIn bookingDtoIn;
    protected BookingDtoOut bookingDtoOut;
    protected ShortBookingDtoOut shortBookingDtoOut;

    protected LocalDateTime now;

    @BeforeEach
    protected void setUp() {
        now = LocalDateTime.now();

        owner = User.builder()
                .id(1)
                .name("owner")
                .email("owner@gmail.com")
                .build();

        item = Item.builder()
                .id(1)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        booker = User.builder()
                .id(2)
                .name("booker")
                .email("booker@gmail.com")
                .build();
        booking = Booking.builder()
                .id(1)
                .startDate(now.plusDays(1))
                .endDate(now.plusDays(2))
                .booker(booker)
                .item(item)
                .status(WAITING)
                .build();

        itemDtoOut = ItemDtoOut.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();

        bookerDtoOut = UserDtoOut.builder()
                .id(booker.getId())
                .name(booker.getName())
                .email(booker.getEmail())
                .build();

        bookingDtoOut = BookingDtoOut.builder()
                .id(booking.getId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .item(itemDtoOut)
                .booker(bookerDtoOut)
                .status(WAITING)
                .build();

        bookingDtoIn = BookingDtoIn.builder()
                .itemId(item.getId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .build();

        shortBookingDtoOut = ShortBookingDtoOut.builder()
                .id(booking.getId())
                .bookerId(booker.getId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .build();

    }
}

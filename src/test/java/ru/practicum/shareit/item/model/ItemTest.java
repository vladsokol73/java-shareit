package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemBaseTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

class ItemTest extends ItemBaseTest {

    @Test
    void equalsTest() {
        Item x = Item.builder()
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        Item y = Item.builder()
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        assertThat(x.equals(y) && y.equals(x)).isTrue();
        assertThat(x.hashCode() == y.hashCode()).isTrue();
    }

    @Test
    void requiredArgsConstructorTest() {
        String name = "item";
        String description = "description";
        boolean available = true;

        Item item = new Item(name, description, available, owner);

        assertThat(item.getName()).isEqualTo(name);
        assertThat(item.getDescription()).isEqualTo(description);
        assertThat(item.getAvailable()).isEqualTo(available);
        assertThat(item.getOwner()).isEqualTo(owner);
    }

    @Test
    void noArgsConstructorTest() {
        int id = 1;
        String name = "item";
        String description = "description";
        boolean available = true;

        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);

        User booker = User.builder()
                .id(2)
                .name("booker")
                .email("booker@gmail.com")
                .build();
        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.builder()
                .startDate(now.minusDays(2))
                .endDate(now.minusDays(1))
                .item(item)
                .booker(booker)
                .status(WAITING)
                .build();

        item.setLastBooking(booking);
        item.setNextBooking(booking);
        item.setBookings(List.of(booking));

        User commentator = User.builder()
                .id(1)
                .name("commentator")
                .email("commentator@gmail.com")
                .build();
        Comment comment = new Comment("comment", item, commentator);
        item.setComments(List.of(comment));

        User requestor = User.builder()
                .id(3)
                .name("requestor")
                .email("requestor@gmail.com")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .description("description")
                .requestor(requestor)
                .build();
        item.setRequest(itemRequest);

        assertThat(item.getId()).isEqualTo(id);
        assertThat(item.getName()).isEqualTo(name);
        assertThat(item.getDescription()).isEqualTo(description);
        assertThat(item.getAvailable()).isEqualTo(available);
        assertThat(item.getNextBooking()).isEqualTo(booking);
        assertThat(item.getLastBooking()).isEqualTo(booking);
        assertThat(item.getBookings()).isEqualTo(List.of(booking));
        assertThat(item.getComments()).isEqualTo(List.of(comment));
        assertThat(item.getRequest()).isEqualTo(itemRequest);
    }

    @Test
    void toStringTest() {
        User owner = User.builder()
                .id(1)
                .name("owner")
                .email("owner@gmail.com")
                .build();


        String name = "item";
        String description = "description";
        boolean available = true;

        Item item = new Item(name, description, available, owner);

        assertThat(item.toString()).startsWith(item.getClass().getSimpleName());
    }
}
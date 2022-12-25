package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import ru.practicum.shareit.booking.dto.ShortBookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

public class ItemBaseTest {

    protected User requestor, owner, commentator, booker;

    protected ItemRequest itemRequest;

    protected Item item;
    protected ItemDtoIn itemDtoIn;
    protected ItemDtoOut itemDtoOut;
    protected FullItemDtoOut fullItemDtoOut;

    protected Booking booking;
    protected ShortBookingDtoOut shortBookingDtoOut;

    protected Comment comment;
    protected CommentDtoOut commentDtoOut;
    protected CommentDtoIn commentDtoIn;

    protected LocalDateTime now;

    @BeforeEach
    protected void setUp() {
        now = LocalDateTime.now().withNano(0);

        requestor = User.builder()
                .id(1)
                .name("requestor")
                .email("requestor@gmail.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1)
                .description("description")
                .requestor(requestor)
                .build();

        owner = User.builder()
                .id(2)
                .name("owner")
                .email("owner@gmail.com")
                .build();
        item = Item.builder()
                .id(1)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .comments(Collections.emptyList())
                .build();

        commentator = User.builder()
                .id(3)
                .name("commentator")
                .email("commentator@gmail.com")
                .build();

        comment = Comment.builder()
                .id(1)
                .text("comment")
                .item(item)
                .author(commentator)
                .created(now)
                .build();
        item.setComments(List.of(comment));

        booker = User.builder()
                .id(4)
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
        item.setLastBooking(booking);
        item.setNextBooking(booking);
        item.setBookings(List.of(booking));



        itemDtoIn = ItemDtoIn.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(true)
                .requestId(itemRequest.getId())
                .build();

        itemDtoOut = ItemDtoOut.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(true)
                .requestId(itemRequest.getId())
                .build();


        shortBookingDtoOut = ShortBookingDtoOut.builder()
                .id(1)
                .bookerId(booker.getId())
                .startDate(now.plusDays(1))
                .endDate(now.plusDays(2))
                .build();

        commentDtoIn = new CommentDtoIn(comment.getText());
        commentDtoOut = CommentDtoOut.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();


        fullItemDtoOut = FullItemDtoOut.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(true)
                .requestId(itemRequest.getId())
                .nextBooking(shortBookingDtoOut)
                .lastBooking(shortBookingDtoOut)
                .comments(List.of(commentDtoOut))
                .build();
    }
}

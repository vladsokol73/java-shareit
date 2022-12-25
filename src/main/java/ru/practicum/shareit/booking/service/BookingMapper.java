package ru.practicum.shareit.booking.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.ShortBookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.service.CommentMapper;
import ru.practicum.shareit.item.service.ItemFactory;
import ru.practicum.shareit.user.service.UserFactory;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {UserService.class, UserMapper.class, ItemFactory.class,   CommentMapper.class, UserFactory.class}
)
public interface BookingMapper {

    BookingDtoOut toDto(Booking booking);

    List<BookingDtoOut> toDto(List<Booking> bookings);

    @Mapping(source = "booker.id", target = "bookerId")
    ShortBookingDtoOut toShortDto(Booking booking);

    @Mapping(source = "bookingDto.itemId", target = "item")
    @Mapping(source = "userId", target = "booker")
    Booking fromDto(BookingDtoIn bookingDto, Integer userId);
}

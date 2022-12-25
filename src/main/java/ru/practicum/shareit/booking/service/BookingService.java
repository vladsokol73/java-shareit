package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOutAbs;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

public interface BookingService {

    BookingDtoOutAbs create(BookingDtoIn bookingDtoIn, int userId);

    BookingDtoOutAbs getById(int id, int userId);

    List<? extends BookingDtoOutAbs> getAllByBooker(int userId, int from, int size);

    List<? extends BookingDtoOutAbs> getBookingByOwnerAndStatus(int userId, BookingStatus status, int from, int size);

    List<? extends BookingDtoOutAbs> getPastBookingByOwner(int userId, int from, int size);

    List<? extends BookingDtoOutAbs> getCurrentBookingByOwner(int userId, int from, int size);

    List<? extends BookingDtoOutAbs> getFutureBookingByOwner(int userId, int from, int size);

    List<? extends BookingDtoOutAbs> getAllByOwner(int ownerId, int from, int size);

    List<? extends BookingDtoOutAbs> getBookingByBookerAndStatus(int userId, BookingStatus status, int from, int size);

    List<? extends BookingDtoOutAbs> getPastBookingByBooker(int userId, int from, int size);

    List<? extends BookingDtoOutAbs> getCurrentBookingByBooker(int userId, int from, int size);

    List<? extends BookingDtoOutAbs> getFutureBookingByBooker(int userId, int from, int size);

    BookingDtoOutAbs approve(int id, int ownerId, boolean approved);

    void delete(int id);
}

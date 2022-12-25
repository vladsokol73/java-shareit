package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOutAbs;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.exception.UnsupportedStatusException;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.user.controller.UserController.USER_ID_HEADER;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    private static final String DEFAULT_PAGE_SIZE = "100";

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BookingDtoOutAbs create(@RequestHeader(value = USER_ID_HEADER) int userId,
                                   @Valid @RequestBody BookingDtoIn bookingDtoIn,
                                   BindingResult result) {
        if (result.getErrorCount() != 0) {
            log.error("Validation errors: {}", result.getAllErrors());
            throw new ValidationException();
        }

        return bookingService.create(bookingDtoIn, userId);
    }

    @GetMapping("/{id}")
    public BookingDtoOutAbs getById(@PathVariable("id") int id,
                                    @RequestHeader(value = USER_ID_HEADER) int ownerId) {
        return bookingService.getById(id, ownerId);
    }

    @GetMapping
    public List<? extends BookingDtoOutAbs> getAllByBooker(@RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
                                                           @RequestHeader(value = USER_ID_HEADER) int userId,
                                                           @Min(0) @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                           @Min(1) @RequestParam(value = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer size) {
        switch (state) {
            case "ALL":
                return bookingService.getAllByBooker(userId, from, size);
            case "CURRENT":
                return bookingService.getCurrentBookingByBooker(userId, from, size);
            case "PAST":
                return bookingService.getPastBookingByBooker(userId, from, size);
            case "FUTURE":
                return bookingService.getFutureBookingByBooker(userId, from, size);
            case "WAITING":
                return bookingService.getBookingByBookerAndStatus(userId, BookingStatus.WAITING, from, size);
            case "REJECTED":
                return bookingService.getBookingByBookerAndStatus(userId, BookingStatus.REJECTED, from, size);
            default:
                throw new UnsupportedStatusException(state);
        }
    }

    @GetMapping("/owner")
    public List<? extends BookingDtoOutAbs> getAllByOwner(@RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
                                                          @RequestHeader(value = USER_ID_HEADER) int userId,
                                                          @Min(0) @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                          @Min(1) @RequestParam(value = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer size) {
        switch (state) {
            case "ALL":
                return bookingService.getAllByOwner(userId, from, size);
            case "CURRENT":
                return bookingService.getCurrentBookingByOwner(userId, from, size);
            case "PAST":
                return bookingService.getPastBookingByOwner(userId, from, size);
            case "FUTURE":
                return bookingService.getFutureBookingByOwner(userId, from, size);
            case "WAITING":
                return bookingService.getBookingByOwnerAndStatus(userId, BookingStatus.WAITING, from, size);
            case "REJECTED":
                return bookingService.getBookingByOwnerAndStatus(userId, BookingStatus.REJECTED, from, size);
            default:
                throw new UnsupportedStatusException(state);
        }
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOutAbs setApproved(@PathVariable("bookingId") int id,
                                        @RequestParam("approved") boolean isApproved,
                                        @RequestHeader(value = USER_ID_HEADER) int ownerId) {

        return bookingService.approve(id, ownerId, isApproved);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int id) {
        bookingService.delete(id);
    }
}

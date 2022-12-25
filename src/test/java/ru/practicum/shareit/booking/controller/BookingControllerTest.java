package ru.practicum.shareit.booking.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingBaseTest;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.ErrorHandler;
import ru.practicum.shareit.util.exception.ForbiddenException;
import ru.practicum.shareit.util.exception.ItemIsNotAvailableException;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.StatusChangeException;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.user.controller.UserController.USER_ID_HEADER;

@SpringBootTest
public class BookingControllerTest extends BookingBaseTest {

    @Autowired
    private BookingController controller;

    @MockBean
    private BookingService bookingService;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    @BeforeEach
    protected void setUp() {
        super.setUp();

        mapper.registerModule(new JavaTimeModule());

        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ErrorHandler.class)
                .build();
    }

    @SneakyThrows
    @Test
    void create_shouldAnswerOK() {
        when(bookingService.create(any(BookingDtoIn.class), anyInt()))
                .thenReturn(bookingDtoOut);

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, "1")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(booking.getId())))
                .andExpect(jsonPath("$.start", Matchers.is(booking.getStartDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.end", Matchers.is(booking.getEndDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.status", Matchers.is(booking.getStatus().name())))
                .andExpect(jsonPath("$.item.id", Matchers.is(booking.getItem().getId())))
                .andExpect(jsonPath("$.item.name", Matchers.is(booking.getItem().getName())))
                .andExpect(jsonPath("$.item.description", Matchers.is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", Matchers.is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$.item.requestId").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.booker.id", Matchers.is(booking.getBooker().getId())))
                .andExpect(jsonPath("$.booker.name", Matchers.is(booking.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", Matchers.is(booking.getBooker().getEmail())));
    }

    @SneakyThrows
    @Test
    void create_shouldAnswerBadRequestWhenStartAndEndInPast() {
        bookingDtoIn.setStartDate(now.minusDays(2));
        bookingDtoIn.setEndDate(now.minusDays(1));

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, "1")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void create_shouldAnswerBadRequestWhenStartInPast() {
        bookingDtoIn.setStartDate(now.minusDays(2));
        bookingDtoIn.setEndDate(now.plusDays(1));

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, "1")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @SneakyThrows
    @Test
    void create_shouldAnswerBadRequestWhenStartAndEndAreMixedUp() {
        bookingDtoIn.setStartDate(now.plusDays(2));
        bookingDtoIn.setEndDate(now.minusDays(1));

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, "1")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void create_shouldAnswerBadRequestWhenUserIdIsMissing() {
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void create_shouldAnswerBadRequestWhenItemIsNotAvailable() {
        when(bookingService.create(any(BookingDtoIn.class), anyInt()))
                .thenThrow(new ItemIsNotAvailableException());

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, "1")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void create_shouldAnswerBadRequestWhenItemDoesNotFounded() {
        when(bookingService.create(any(BookingDtoIn.class), anyInt()))
                .thenThrow(new NotFoundException("The booker can not be the owner"));

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, "1")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getById_shouldAnswerOK() {
        when(bookingService.getById(anyInt(), anyInt()))
                .thenReturn(bookingDtoOut);

        mvc.perform(get("/bookings/1")
                        .header(USER_ID_HEADER, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(booking.getId())))
                .andExpect(jsonPath("$.start", Matchers.is(booking.getStartDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.end", Matchers.is(booking.getEndDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.status", Matchers.is(booking.getStatus().name())))
                .andExpect(jsonPath("$.item.id", Matchers.is(booking.getItem().getId())))
                .andExpect(jsonPath("$.item.name", Matchers.is(booking.getItem().getName())))
                .andExpect(jsonPath("$.item.description", Matchers.is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", Matchers.is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$.item.requestId").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.booker.id", Matchers.is(booking.getBooker().getId())))
                .andExpect(jsonPath("$.booker.name", Matchers.is(booking.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", Matchers.is(booking.getBooker().getEmail())));
    }

    @SneakyThrows
    @Test
    void getById_shouldAnswerBadRequestWhenUserIdIsMissing() {
        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getById_shouldAnswerNotFoundWhenItemIsNotFounded() {
        when(bookingService.getById(anyInt(), anyInt()))
                .thenThrow(new NotFoundException(""));

        mvc.perform(get("/bookings/1")
                        .header(USER_ID_HEADER, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getById_shouldAnswerNotFoundRequestWhenActionIsForbidden() {
        when(bookingService.getById(anyInt(), anyInt()))
                .thenThrow(new ForbiddenException());

        mvc.perform(get("/bookings/1")
                        .header(USER_ID_HEADER, "1")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getAllByBooker_shouldAnswerOK() {
        doReturn(List.of(bookingDtoOut)).when(bookingService)
                .getAllByBooker(anyInt(), anyInt(), anyInt());

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, "1")
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(booking.getId())))
                .andExpect(jsonPath("$[0].start", Matchers.is(booking.getStartDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].end", Matchers.is(booking.getEndDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].status", Matchers.is(booking.getStatus().name())))
                .andExpect(jsonPath("$[0].item.id", Matchers.is(booking.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", Matchers.is(booking.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", Matchers.is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", Matchers.is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].item.requestId").value(Matchers.nullValue()))
                .andExpect(jsonPath("$[0].booker.id", Matchers.is(booking.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", Matchers.is(booking.getBooker().getName())))
                .andExpect(jsonPath("$[0].booker.email", Matchers.is(booking.getBooker().getEmail())));
    }

    @SneakyThrows
    @Test
    void getAllByBooker_shouldReturnBadWhenStatusIsUnsupported() {
        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, "1")
                        .param("state", "UNKNOWN")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllByBooker_shouldAnswerOKWhenStateIsMissing() {
        doReturn(List.of(bookingDtoOut)).when(bookingService)
                .getAllByBooker(anyInt(), anyInt(), anyInt());

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, "1")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(booking.getId())))
                .andExpect(jsonPath("$[0].start", Matchers.is(booking.getStartDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].end", Matchers.is(booking.getEndDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].status", Matchers.is(booking.getStatus().name())))
                .andExpect(jsonPath("$[0].item.id", Matchers.is(booking.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", Matchers.is(booking.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", Matchers.is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", Matchers.is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].item.requestId").value(Matchers.nullValue()))
                .andExpect(jsonPath("$[0].booker.id", Matchers.is(booking.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", Matchers.is(booking.getBooker().getName())))
                .andExpect(jsonPath("$[0].booker.email", Matchers.is(booking.getBooker().getEmail())));
    }

    @SneakyThrows
    @Test
    void getAllByBooker_shouldAnswerOKAndCurrentBooking() {
        doReturn(List.of(bookingDtoOut)).when(bookingService)
                .getCurrentBookingByBooker(anyInt(), anyInt(), anyInt());

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, "1")
                        .param("state", "CURRENT")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(booking.getId())))
                .andExpect(jsonPath("$[0].start", Matchers.is(booking.getStartDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].end", Matchers.is(booking.getEndDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].status", Matchers.is(booking.getStatus().name())))
                .andExpect(jsonPath("$[0].item.id", Matchers.is(booking.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", Matchers.is(booking.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", Matchers.is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", Matchers.is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].item.requestId").value(Matchers.nullValue()))
                .andExpect(jsonPath("$[0].booker.id", Matchers.is(booking.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", Matchers.is(booking.getBooker().getName())))
                .andExpect(jsonPath("$[0].booker.email", Matchers.is(booking.getBooker().getEmail())));
    }


    @SneakyThrows
    @Test
    void getAllByBooker_shouldAnswerOKAndPastBooking() {
        doReturn(List.of(bookingDtoOut)).when(bookingService)
                .getPastBookingByBooker(anyInt(), anyInt(), anyInt());

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, "1")
                        .param("state", "PAST")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(booking.getId())))
                .andExpect(jsonPath("$[0].start", Matchers.is(booking.getStartDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].end", Matchers.is(booking.getEndDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].status", Matchers.is(booking.getStatus().name())))
                .andExpect(jsonPath("$[0].item.id", Matchers.is(booking.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", Matchers.is(booking.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", Matchers.is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", Matchers.is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].item.requestId").value(Matchers.nullValue()))
                .andExpect(jsonPath("$[0].booker.id", Matchers.is(booking.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", Matchers.is(booking.getBooker().getName())))
                .andExpect(jsonPath("$[0].booker.email", Matchers.is(booking.getBooker().getEmail())));
    }

    @SneakyThrows
    @Test
    void getAllByBooker_shouldAnswerOKAndFutureBooking() {
        doReturn(List.of(bookingDtoOut)).when(bookingService)
                .getFutureBookingByBooker(anyInt(), anyInt(), anyInt());

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, "1")
                        .param("state", "FUTURE")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(booking.getId())))
                .andExpect(jsonPath("$[0].start", Matchers.is(booking.getStartDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].end", Matchers.is(booking.getEndDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].status", Matchers.is(booking.getStatus().name())))
                .andExpect(jsonPath("$[0].item.id", Matchers.is(booking.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", Matchers.is(booking.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", Matchers.is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", Matchers.is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].item.requestId").value(Matchers.nullValue()))
                .andExpect(jsonPath("$[0].booker.id", Matchers.is(booking.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", Matchers.is(booking.getBooker().getName())))
                .andExpect(jsonPath("$[0].booker.email", Matchers.is(booking.getBooker().getEmail())));
    }

    @SneakyThrows
    @Test
    void getAllByBooker_shouldAnswerOKAnWaitingBooking() {
        doReturn(List.of(bookingDtoOut)).when(bookingService)
                .getBookingByBookerAndStatus(anyInt(), any(BookingStatus.class), anyInt(), anyInt());

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, "1")
                        .param("state", "WAITING")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(booking.getId())))
                .andExpect(jsonPath("$[0].start", Matchers.is(booking.getStartDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].end", Matchers.is(booking.getEndDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].status", Matchers.is(booking.getStatus().name())))
                .andExpect(jsonPath("$[0].item.id", Matchers.is(booking.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", Matchers.is(booking.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", Matchers.is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", Matchers.is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].item.requestId").value(Matchers.nullValue()))
                .andExpect(jsonPath("$[0].booker.id", Matchers.is(booking.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", Matchers.is(booking.getBooker().getName())))
                .andExpect(jsonPath("$[0].booker.email", Matchers.is(booking.getBooker().getEmail())));
    }


    @SneakyThrows
    @Test
    void getAllByBooker_shouldAnswerOKAndRejectedBooking() {
        doReturn(List.of(bookingDtoOut)).when(bookingService)
                .getBookingByBookerAndStatus(anyInt(), any(BookingStatus.class), anyInt(), anyInt());

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, "1")
                        .param("state", "REJECTED")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(booking.getId())))
                .andExpect(jsonPath("$[0].start", Matchers.is(booking.getStartDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].end", Matchers.is(booking.getEndDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].status", Matchers.is(booking.getStatus().name())))
                .andExpect(jsonPath("$[0].item.id", Matchers.is(booking.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", Matchers.is(booking.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", Matchers.is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", Matchers.is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].item.requestId").value(Matchers.nullValue()))
                .andExpect(jsonPath("$[0].booker.id", Matchers.is(booking.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", Matchers.is(booking.getBooker().getName())))
                .andExpect(jsonPath("$[0].booker.email", Matchers.is(booking.getBooker().getEmail())));
    }

    @SneakyThrows
    @Test
    void getAllByBooker_shouldAnswerBadRequestWhenUserIdIsMissing() {
        mvc.perform(get("/bookings")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllByBooker_shouldAnswerBadRequestWhenParamFromIsNegative() {
        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, "1")
                        .param("state", "REJECTED")
                        .param("from", "-1")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllByBooker_shouldAnswerBadRequestWhenParamToIsZero() {
        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, "1")
                        .param("state", "REJECTED")
                        .param("from", "0")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllByBooker_shouldAnswerBadRequestWhenParamToIsNegative() {
        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, "1")
                        .param("state", "REJECTED")
                        .param("from", "0")
                        .param("size", "-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllByOwner_shouldAnswerOK() {
        doReturn(List.of(bookingDtoOut)).when(bookingService)
                .getAllByOwner(anyInt(), anyInt(), anyInt());

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, "1")
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(booking.getId())))
                .andExpect(jsonPath("$[0].start", Matchers.is(booking.getStartDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].end", Matchers.is(booking.getEndDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].status", Matchers.is(booking.getStatus().name())))
                .andExpect(jsonPath("$[0].item.id", Matchers.is(booking.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", Matchers.is(booking.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", Matchers.is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", Matchers.is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].item.requestId").value(Matchers.nullValue()))
                .andExpect(jsonPath("$[0].booker.id", Matchers.is(booking.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", Matchers.is(booking.getBooker().getName())))
                .andExpect(jsonPath("$[0].booker.email", Matchers.is(booking.getBooker().getEmail())));
    }

    @SneakyThrows
    @Test
    void getAllByOwner_shouldReturnBadWhenStatusIsUnsupported() {
        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, "1")
                        .param("state", "UNKNOWN")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllByOwner_shouldAnswerOKWhenStateIsMissing() {
        doReturn(List.of(bookingDtoOut)).when(bookingService)
                .getAllByOwner(anyInt(), anyInt(), anyInt());

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, "1")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(booking.getId())))
                .andExpect(jsonPath("$[0].start", Matchers.is(booking.getStartDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].end", Matchers.is(booking.getEndDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].status", Matchers.is(booking.getStatus().name())))
                .andExpect(jsonPath("$[0].item.id", Matchers.is(booking.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", Matchers.is(booking.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", Matchers.is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", Matchers.is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].item.requestId").value(Matchers.nullValue()))
                .andExpect(jsonPath("$[0].booker.id", Matchers.is(booking.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", Matchers.is(booking.getBooker().getName())))
                .andExpect(jsonPath("$[0].booker.email", Matchers.is(booking.getBooker().getEmail())));
    }

    @SneakyThrows
    @Test
    void getAllByOwner_shouldAnswerOKAndCurrentBooking() {
        doReturn(List.of(bookingDtoOut)).when(bookingService)
                .getCurrentBookingByOwner(anyInt(), anyInt(), anyInt());

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, "1")
                        .param("state", "CURRENT")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(booking.getId())))
                .andExpect(jsonPath("$[0].start", Matchers.is(booking.getStartDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].end", Matchers.is(booking.getEndDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].status", Matchers.is(booking.getStatus().name())))
                .andExpect(jsonPath("$[0].item.id", Matchers.is(booking.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", Matchers.is(booking.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", Matchers.is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", Matchers.is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].item.requestId").value(Matchers.nullValue()))
                .andExpect(jsonPath("$[0].booker.id", Matchers.is(booking.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", Matchers.is(booking.getBooker().getName())))
                .andExpect(jsonPath("$[0].booker.email", Matchers.is(booking.getBooker().getEmail())));
    }




    @SneakyThrows
    @Test
    void getAllByOwner_shouldAnswerOKAndPastBooking() {
        doReturn(List.of(bookingDtoOut)).when(bookingService)
                .getPastBookingByOwner(anyInt(), anyInt(), anyInt());

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, "1")
                        .param("state", "PAST")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(booking.getId())))
                .andExpect(jsonPath("$[0].start", Matchers.is(booking.getStartDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].end", Matchers.is(booking.getEndDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].status", Matchers.is(booking.getStatus().name())))
                .andExpect(jsonPath("$[0].item.id", Matchers.is(booking.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", Matchers.is(booking.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", Matchers.is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", Matchers.is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].item.requestId").value(Matchers.nullValue()))
                .andExpect(jsonPath("$[0].booker.id", Matchers.is(booking.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", Matchers.is(booking.getBooker().getName())))
                .andExpect(jsonPath("$[0].booker.email", Matchers.is(booking.getBooker().getEmail())));
    }

    @SneakyThrows
    @Test
    void getAllByOwner_shouldAnswerOKAndFutureBooking() {
        doReturn(List.of(bookingDtoOut)).when(bookingService)
                .getFutureBookingByOwner(anyInt(), anyInt(), anyInt());

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, "1")
                        .param("state", "FUTURE")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(booking.getId())))
                .andExpect(jsonPath("$[0].start", Matchers.is(booking.getStartDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].end", Matchers.is(booking.getEndDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].status", Matchers.is(booking.getStatus().name())))
                .andExpect(jsonPath("$[0].item.id", Matchers.is(booking.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", Matchers.is(booking.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", Matchers.is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", Matchers.is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].item.requestId").value(Matchers.nullValue()))
                .andExpect(jsonPath("$[0].booker.id", Matchers.is(booking.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", Matchers.is(booking.getBooker().getName())))
                .andExpect(jsonPath("$[0].booker.email", Matchers.is(booking.getBooker().getEmail())));
    }

    @SneakyThrows
    @Test
    void getAllByOwner_shouldAnswerOKAnWaitingBooking() {
        doReturn(List.of(bookingDtoOut)).when(bookingService)
                .getBookingByOwnerAndStatus(anyInt(), any(BookingStatus.class), anyInt(), anyInt());

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, "1")
                        .param("state", "WAITING")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(booking.getId())))
                .andExpect(jsonPath("$[0].start", Matchers.is(booking.getStartDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].end", Matchers.is(booking.getEndDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].status", Matchers.is(booking.getStatus().name())))
                .andExpect(jsonPath("$[0].item.id", Matchers.is(booking.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", Matchers.is(booking.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", Matchers.is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", Matchers.is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].item.requestId").value(Matchers.nullValue()))
                .andExpect(jsonPath("$[0].booker.id", Matchers.is(booking.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", Matchers.is(booking.getBooker().getName())))
                .andExpect(jsonPath("$[0].booker.email", Matchers.is(booking.getBooker().getEmail())));
    }

    @SneakyThrows
    @Test
    void getAllByOwner_shouldAnswerOKAndRejectedBooking() {
        doReturn(List.of(bookingDtoOut)).when(bookingService)
                .getBookingByOwnerAndStatus(anyInt(), any(BookingStatus.class), anyInt(), anyInt());

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, "1")
                        .param("state", "REJECTED")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(booking.getId())))
                .andExpect(jsonPath("$[0].start", Matchers.is(booking.getStartDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].end", Matchers.is(booking.getEndDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].status", Matchers.is(booking.getStatus().name())))
                .andExpect(jsonPath("$[0].item.id", Matchers.is(booking.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", Matchers.is(booking.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", Matchers.is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", Matchers.is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].item.requestId").value(Matchers.nullValue()))
                .andExpect(jsonPath("$[0].booker.id", Matchers.is(booking.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", Matchers.is(booking.getBooker().getName())))
                .andExpect(jsonPath("$[0].booker.email", Matchers.is(booking.getBooker().getEmail())));
    }

    @SneakyThrows
    @Test
    void getAllByOwner_shouldAnswerBadRequestWhenUserIdIsMissing() {
        mvc.perform(get("/bookings/owner")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllByOwner_shouldAnswerBadRequestWhenParamFromIsNegative() {
        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, "1")
                        .param("state", "REJECTED")
                        .param("from", "-1")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllByOwner_shouldAnswerBadRequestWhenParamToIsZero() {
        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, "1")
                        .param("state", "REJECTED")
                        .param("from", "0")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllByOwner_shouldAnswerBadRequestWhenParamToIsNegative() {
        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, "1")
                        .param("state", "REJECTED")
                        .param("from", "0")
                        .param("size", "-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void setApproved_shouldAnswerOK() {

        when(bookingService.approve(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(bookingDtoOut);

        mvc.perform(patch("/bookings/1")
                        .header(USER_ID_HEADER, "1")
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(booking.getId())))
                .andExpect(jsonPath("$.start", Matchers.is(booking.getStartDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.end", Matchers.is(booking.getEndDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.status", Matchers.is(booking.getStatus().name())))
                .andExpect(jsonPath("$.item.id", Matchers.is(booking.getItem().getId())))
                .andExpect(jsonPath("$.item.name", Matchers.is(booking.getItem().getName())))
                .andExpect(jsonPath("$.item.description", Matchers.is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", Matchers.is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$.item.requestId").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.booker.id", Matchers.is(booking.getBooker().getId())))
                .andExpect(jsonPath("$.booker.name", Matchers.is(booking.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", Matchers.is(booking.getBooker().getEmail())));
    }

    @SneakyThrows
    @Test
    void setApproved_shouldAnswerBadRequestWhenUserIdIsMissing() {
        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void setApproved_shouldAnswerBadRequestWhenParamApprovedIsMissing() {
        mvc.perform(patch("/bookings/1")
                        .header(USER_ID_HEADER, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void approve_shouldAnswerNotFoundRequestWhenActionIsForbidden() {
        when(bookingService.approve(anyInt(), anyInt(), anyBoolean()))
                .thenThrow(new ForbiddenException());

        mvc.perform(patch("/bookings/1")
                        .header(USER_ID_HEADER, "1")
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void approve_shouldAnswerNotFoundRequestWhenItemStatusIsNotWaiting() {
        when(bookingService.approve(anyInt(), anyInt(), anyBoolean()))
                .thenThrow(new StatusChangeException());

        mvc.perform(patch("/bookings/1")
                        .header(USER_ID_HEADER, "1")
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void delete_shouldAnswerOK() {
        mvc.perform(delete("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void delete_shouldBadRequestWhenUserIsNotFound() {
        doThrow(new NotFoundException("")).when(bookingService).delete(anyInt());

        mvc.perform(delete("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

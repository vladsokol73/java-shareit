package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.StatusDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ControllerTest {
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private final BookingMapper bookingMapper = new BookingMapper();

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final BookingDto bookingDto1 = new BookingDto();
    private Booking booking1;
    private Booking booking2;
    private final User user1 = new User();
    private final User user2 = new User();
    private final Item item = new Item();

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        objectMapper.findAndRegisterModules();
        user1.setId(1);
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        user2.setId(2);
        user2.setName("user2");
        user2.setEmail("u2@user.com");
        item.setId(1);
        item.setName("item1");
        item.setDescription("descr item1");
        item.setAvailable(true);
        item.setOwner(user1.getId());
        bookingDto1.setId(1);
        bookingDto1.setItemId(item.getId());
        bookingDto1.setUserId(user2.getId());
        bookingDto1.setStart(LocalDateTime.now().plusHours(1));
        bookingDto1.setEnd(LocalDateTime.now().plusHours(2));
        bookingDto1.setStatus(Status.WAITING);
        bookingDto1.setBooker(user2);
        bookingDto1.setItem(item);

        booking1 = bookingMapper.toBooking(bookingDto1, user2.getId());
        booking2 = bookingMapper.toBooking(bookingDto1, user2.getId());
        booking2.setId(2);
        booking2.setStart(LocalDateTime.now().plusHours(5));
        booking2.setEnd(LocalDateTime.now().plusHours(6));
    }

    @Test
    public void addBookingCheckStatusTest() throws Exception {
        when(bookingService.add(Mockito.any(BookingDto.class), Mockito.anyInt()))
                .thenReturn(bookingMapper.toDto(booking1, item, user2));
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingMapper.toDto(booking1, item, user2)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk());
    }

    @Test
    public void addBookingCheckJsonContentTest1() throws Exception {
        when(bookingService.add(Mockito.any(BookingDto.class), Mockito.anyInt()))
                .thenReturn(bookingMapper.toDto(booking1, item, user2));
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingMapper.toDto(booking1, item, user2)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(jsonPath("$.booker.id", is(bookingDto1.getBooker().getId())))
                .andExpect(jsonPath("$.booker.name", is(bookingDto1.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(bookingDto1.getBooker().getEmail())));
    }

    @Test
    public void addBookingCheckJsonContentTest2() throws Exception {
        when(bookingService.add(Mockito.any(BookingDto.class), Mockito.anyInt()))
                .thenReturn(bookingMapper.toDto(booking1, item, user2));
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingMapper.toDto(booking1, item, user2)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(jsonPath("$.item.id", is(bookingDto1.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingDto1.getItem().getName())))
                .andExpect(jsonPath("$.item.owner", is(bookingDto1.getItem().getOwner())))
                .andExpect(jsonPath("$.item.description", is(bookingDto1.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(bookingDto1.getItem().getAvailable())));
    }

    @Test
    public void addBookingCheckJsonContentTest3() throws Exception {
        when(bookingService.add(Mockito.any(BookingDto.class), Mockito.anyInt()))
                .thenReturn(bookingMapper.toDto(booking1, item, user2));
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingMapper.toDto(booking1, item, user2)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto1)));
    }

    @Test
    public void addBookingWithInvalidDate() throws Exception {
        booking1.setStart(null);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingMapper.toDto(booking1, item, user2)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void updApproveCheckStatusIsOkTest() throws Exception {
        booking1.setStatus(Status.APPROVED);
        when(bookingService.updApprove(Mockito.anyInt(), Mockito.anyBoolean(), Mockito.anyInt()))
                .thenReturn(bookingMapper.toDto(booking1, item, user2));

        mockMvc.perform(patch("/bookings/{bookingId}", booking1.getId())
                        .content(objectMapper.writeValueAsString(bookingMapper.toDto(booking1, item, user2)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .param("approved", "true"))
                .andExpect(status().isOk());

    }

    @Test
    public void updApproveCheckJsonContentTest1() throws Exception {
        booking1.setStatus(Status.APPROVED);
        when(bookingService.updApprove(Mockito.anyInt(), Mockito.anyBoolean(), Mockito.anyInt()))
                .thenReturn(bookingMapper.toDto(booking1, item, user2));

        mockMvc.perform(patch("/bookings/{bookingId}", booking1.getId())
                        .content(objectMapper.writeValueAsString(bookingMapper.toDto(booking1, item, user2)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .param("approved", "true"))
                .andExpect(jsonPath("$.booker.id", is(bookingDto1.getBooker().getId())))
                .andExpect(jsonPath("$.booker.name", is(bookingDto1.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(bookingDto1.getBooker().getEmail())));

    }

    @Test
    public void updApproveCheckJsonContentTest2() throws Exception {
        booking1.setStatus(Status.APPROVED);
        when(bookingService.updApprove(Mockito.anyInt(), Mockito.anyBoolean(), Mockito.anyInt()))
                .thenReturn(bookingMapper.toDto(booking1, item, user2));

        mockMvc.perform(patch("/bookings/{bookingId}", booking1.getId())
                        .content(objectMapper.writeValueAsString(bookingMapper.toDto(booking1, item, user2)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .param("approved", "true"))
                .andExpect(jsonPath("$.item.id", is(bookingDto1.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingDto1.getItem().getName())))
                .andExpect(jsonPath("$.item.owner", is(bookingDto1.getItem().getOwner())))
                .andExpect(jsonPath("$.item.description", is(bookingDto1.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(bookingDto1.getItem().getAvailable())));
    }

    @Test
    public void updApproveCheckJsonContentTest3() throws Exception {
        booking1.setStatus(Status.APPROVED);
        when(bookingService.updApprove(Mockito.anyInt(), Mockito.anyBoolean(), Mockito.anyInt()))
                .thenReturn(bookingMapper.toDto(booking1, item, user2));

        mockMvc.perform(patch("/bookings/{bookingId}", booking1.getId())
                        .content(objectMapper.writeValueAsString(bookingMapper.toDto(booking1, item, user2)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .param("approved", "true"))
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(bookingMapper.toDto(booking1, item, user2))));
    }

    @Test
    public void findBookingByIdCheckStatusIsOkTest() throws Exception {
        when(bookingService.findById(1, 2))
                .thenReturn(bookingDto1);

        mockMvc.perform(get("/bookings/{bookingId}", booking1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk());

    }

    @Test
    public void findBookingByIdCheckJsonContentTest() throws Exception {
        when(bookingService.findById(1, 2))
                .thenReturn(bookingDto1);

        mockMvc.perform(get("/bookings/{bookingId}", booking1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(content().json(objectMapper
                        .writeValueAsString(bookingMapper.toDto(booking1, item, user2))));

    }

    @Test
    public void findAllBookingsByUserDefaultParamCheckStatusIsOkTest() throws Exception {
        BookingDto bookingDto2 = bookingMapper.toDto(booking2, item, user2);
        when(bookingService.findAllByUser(Mockito.anyInt(), Mockito.any(StatusDto.class),
                Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDto1, bookingDto2));

        mockMvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk());

    }

    @Test
    public void findAllBookingsByUserDefaultParamCheckJsonContentTest() throws Exception {
        BookingDto bookingDto2 = bookingMapper.toDto(booking2, item, user2);
        when(bookingService.findAllByUser(Mockito.anyInt(), Mockito.any(StatusDto.class),
                Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDto1, bookingDto2));

        mockMvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingDto1, bookingDto2))));

    }

    @Test
    public void findAllBookingsByUserWithCustomParamCheckStatusIsOkTest() throws Exception {
        BookingDto bookingDto2 = bookingMapper.toDto(booking2, item, user2);
        when(bookingService.findAllByUser(Mockito.anyInt(), Mockito.any(StatusDto.class),
                Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDto1, bookingDto2));

        mockMvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

    }

    @Test
    public void findAllBookingsByUserWithCustomParamCheckJsonContentTest() throws Exception {
        BookingDto bookingDto2 = bookingMapper.toDto(booking2, item, user2);
        when(bookingService.findAllByUser(Mockito.anyInt(), Mockito.any(StatusDto.class),
                Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDto1, bookingDto2));

        mockMvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingDto1, bookingDto2))));

    }

    @Test
    public void findAllByOwnerDefaultParamCheckStatusIsOkTest() throws Exception {
        BookingDto bookingDto2 = bookingMapper.toDto(booking2, item, user2);
        when(bookingService.findAllByOwner(Mockito.anyInt(), Mockito.any(StatusDto.class),
                Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDto1, bookingDto2));

        mockMvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk());

    }

    @Test
    public void findAllByOwnerDefaultParamCheckJsonContentTest() throws Exception {
        BookingDto bookingDto2 = bookingMapper.toDto(booking2, item, user2);
        when(bookingService.findAllByOwner(Mockito.anyInt(), Mockito.any(StatusDto.class),
                Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDto1, bookingDto2));

        mockMvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingDto1, bookingDto2))));

    }

    @Test
    public void findAllByOwnerCustomParamCheckStatusIsOkTest() throws Exception {
        BookingDto bookingDto2 = bookingMapper.toDto(booking2, item, user2);
        when(bookingService.findAllByOwner(Mockito.anyInt(), Mockito.any(StatusDto.class),
                Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDto1, bookingDto2));

        mockMvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

    }

    @Test
    public void findAllByOwnerCustomParamCheckJsonContentTest() throws Exception {
        BookingDto bookingDto2 = bookingMapper.toDto(booking2, item, user2);
        when(bookingService.findAllByOwner(Mockito.anyInt(), Mockito.any(StatusDto.class),
                Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDto1, bookingDto2));

        mockMvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingDto1, bookingDto2))));

    }

    @Test
    public void fromDtoToBookingMapperTest() {
        Booking bookingResult = bookingMapper.toBooking(bookingDto1, user2.getId());
        Assertions.assertEquals(bookingResult.getId(), bookingDto1.getId());
        Assertions.assertEquals(bookingResult.getItem(), bookingDto1.getItemId());
        Assertions.assertEquals(bookingResult.getBookerId(), bookingDto1.getUserId());
        Assertions.assertEquals(bookingResult.getStatus(), bookingDto1.getStatus());
        Assertions.assertEquals(bookingResult.getStart(), bookingDto1.getStart());
        Assertions.assertEquals(bookingResult.getEnd(), bookingDto1.getEnd());
    }

    @Test
    public void fromBookingToDtoBookingMapperTest() {
        BookingDto bookingDtoResult = bookingMapper.toDto(booking1, item, user2);
        Assertions.assertEquals(bookingDtoResult.getId(), booking1.getId());
        Assertions.assertEquals(bookingDtoResult.getItemId(), booking1.getItem());
        Assertions.assertEquals(bookingDtoResult.getUserId(), booking1.getBookerId());
        Assertions.assertEquals(bookingDtoResult.getItem(), item);
        Assertions.assertEquals(bookingDtoResult.getBooker(), user2);
        Assertions.assertEquals(bookingDtoResult.getStatus(), booking1.getStatus());
        Assertions.assertEquals(bookingDtoResult.getStart(), booking1.getStart());
        Assertions.assertEquals(bookingDtoResult.getEnd(), booking1.getEnd());
    }

}

package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ServiceUnitTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingMapper bookingMapper;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private final BookingDto bookingDto1 = new BookingDto();
    private final BookingDto bookingDto2 = new BookingDto();
    private final Booking booking1 = new Booking();
    private final Booking booking2 = new Booking();
    private final User user1 = new User();
    private final User user2 = new User();
    private final Item item = new Item();

    @BeforeEach
    public void init() {
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

        bookingDto2.setId(2);
        bookingDto2.setItemId(item.getId());
        bookingDto2.setUserId(user2.getId());
        bookingDto2.setStart(LocalDateTime.now().plusHours(3));
        bookingDto2.setEnd(LocalDateTime.now().plusHours(4));
        bookingDto2.setStatus(Status.WAITING);
        bookingDto2.setBooker(user2);
        bookingDto2.setItem(item);

        booking1.setId(1);
        booking1.setStart(LocalDateTime.now().plusHours(1));
        booking1.setEnd(LocalDateTime.now().plusHours(2));
        booking1.setBookerId(2);
        booking1.setItem(1);
        booking1.setStatus(Status.WAITING);

        booking2.setId(2);
        booking2.setStart(LocalDateTime.now().plusHours(3));
        booking2.setEnd(LocalDateTime.now().plusHours(4));
        booking2.setBookerId(2);
        booking2.setItem(1);
        booking2.setStatus(Status.WAITING);
    }

    @Test
    public void addBookingTest() {
        Mockito
                .when(userRepository.findById(2))
                .thenReturn(Optional.of(user2));
        Mockito
                .when(itemRepository.findById(1))
                .thenReturn(Optional.of(item));
        Mockito
                .when(bookingMapper.toDto(booking1, item, user2))
                .thenReturn(bookingDto1);
        Mockito
                .when(bookingMapper.toBooking(bookingDto1, 2))
                .thenReturn(booking1);
        Mockito
                .when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking1);
        BookingDto bookingDtoResult = bookingService.add(bookingDto1, user2.getId());
        Assertions.assertEquals(bookingDto1, bookingDtoResult);
    }

    @Test
    public void updApproveStatusBookingTest() {
        bookingDto1.setStatus(Status.APPROVED);
        Mockito
                .when(userRepository.findById(2))
                .thenReturn(Optional.of(user2));
        Mockito
                .when(itemRepository.findById(1))
                .thenReturn(Optional.of(item));
        Mockito
                .when(bookingMapper.toDto(booking1, item, user2))
                .thenReturn(bookingDto1);
        Mockito
                .when(bookingRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(booking1));
        Mockito
                .when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking1);
        BookingDto bookingDtoResult = bookingService.updApprove(bookingDto1.getId(), true, user1.getId());
        Assertions.assertEquals(bookingDto1, bookingDtoResult);
    }

    @Test
    public void findByBookingIdTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user2));
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(bookingRepository.getById(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Optional.of(booking1));
        Mockito
                .when(bookingMapper.toDto(
                        Mockito.any(Booking.class), Mockito.any(Item.class), Mockito.any(User.class))
                )
                .thenReturn(bookingDto1);
        BookingDto bookingDtoResult = bookingService.findById(booking1.getId(), user2.getId());
        Assertions.assertEquals(bookingDto1, bookingDtoResult);
    }

}

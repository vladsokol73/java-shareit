package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemRequestRepository requestRepository;

    @Autowired
    BookingRepository bookingRepository;

    User owner1, owner2, requestor, booker1, booker2;
    Item item1, item2;
    ItemRequest request;


    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();

        requestor = userRepository.save(User.builder()
                .name("requestor")
                .email("requestor@mail.ru")
                .build());

        request = requestRepository.save(ItemRequest.builder()
                .description("description")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build());

        owner1 = userRepository.save(User.builder()
                .name("owner1")
                .email("owner1@mail.ru")
                .build());

        owner2 = userRepository.save(User.builder()
                .name("owner2")
                .email("owner2@mail.ru")
                .build());

        item1 = itemRepository.save(Item.builder()
                .name("item1")
                .description("description1")
                .available(true)
                .owner(owner1)
                .request(request)
                .build());

        item2 = itemRepository.save(Item.builder()
                .name("item2")
                .description("description2")
                .available(true)
                .owner(owner2)
                .request(request)
                .build());

        booker1 = userRepository.save(User.builder()
                .name("booker1")
                .email("booker1@mail.ru")
                .build());

        booker2 = userRepository.save(User.builder()
                .name("booker2")
                .email("booker2@mail.ru")
                .build());
    }

    @Test
    void getBookingByBooker_shouldInvokeRepositoryAndReturnTheSame() {
        LocalDateTime now = LocalDateTime.now();

        Booking bookingByBooker1 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item1)
                .startDate(now.plusDays(1))
                .endDate(now.plusDays(2))
                .build());

        Booking bookingByBooker2 = bookingRepository.save(Booking.builder()
                .booker(booker2)
                .status(APPROVED)
                .item(item1)
                .startDate(now.plusDays(3))
                .endDate(now.plusDays(4))
                .build());

        List<Booking> bookings = bookingRepository.getBookingByBooker(booker1.getId(), Pageable.unpaged());
        assertThat(bookings)
                .hasSize(1)
                .containsAll(List.of(bookingByBooker1));
    }

    @Test
    void getBookingByBookerAndStatus_shouldInvokeRepositoryAndReturnTheSame() {
        LocalDateTime now = LocalDateTime.now();

        Booking bookingByBooker1WithWaitingStatus = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(WAITING)
                .item(item1)
                .startDate(now.plusDays(1))
                .endDate(now.plusDays(2))
                .build());

        Booking bookingByBooker1WithApprovedStatus = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item1)
                .startDate(now.plusDays(1))
                .endDate(now.plusDays(2))
                .build());

        Booking bookingByBooker2 = bookingRepository.save(Booking.builder()
                .booker(booker2)
                .status(APPROVED)
                .item(item1)
                .startDate(now.plusDays(3))
                .endDate(now.plusDays(4))
                .build());

        List<Booking> bookings = bookingRepository.getBookingByBookerAndStatus(booker1.getId(), APPROVED, Pageable.unpaged());
        assertThat(bookings)
                .hasSize(1)
                .containsAll(List.of(bookingByBooker1WithApprovedStatus));
    }

    @Test
    void getPastBookingByBooker_shouldInvokeRepositoryAndReturnTheSame() {
        LocalDateTime now = LocalDateTime.now();

        Booking pastBookingByBooker1 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(WAITING)
                .item(item1)
                .startDate(now.minusDays(2))
                .endDate(now.minusDays(1))
                .build());

        Booking currentBookingByBooker1 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item1)
                .startDate(now.minusDays(1))
                .endDate(now.plusDays(1))
                .build());

        Booking futureBookingByBooker1 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item1)
                .startDate(now.plusDays(1))
                .endDate(now.plusDays(2))
                .build());

        Booking bookingByBooker2 = bookingRepository.save(Booking.builder()
                .booker(booker2)
                .status(APPROVED)
                .item(item1)
                .startDate(now.plusDays(3))
                .endDate(now.plusDays(4))
                .build());


        List<Booking> bookings = bookingRepository.getPastBookingByBooker(booker1.getId(), Pageable.unpaged());
        assertThat(bookings)
                .hasSize(1)
                .containsAll(List.of(pastBookingByBooker1));
    }

    @Test
    void getCurrentBookingByBooker_shouldInvokeRepositoryAndReturnTheSame() {
        LocalDateTime now = LocalDateTime.now();

        Booking pastBookingByBooker1 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(WAITING)
                .item(item1)
                .startDate(now.minusDays(2))
                .endDate(now.minusDays(1))
                .build());

        Booking currentBookingByBooker1 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item1)
                .startDate(now.minusDays(1))
                .endDate(now.plusDays(1))
                .build());

        Booking futureBookingByBooker1 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item1)
                .startDate(now.plusDays(1))
                .endDate(now.plusDays(2))
                .build());

        Booking bookingByBooker2 = bookingRepository.save(Booking.builder()
                .booker(booker2)
                .status(APPROVED)
                .item(item1)
                .startDate(now.plusDays(3))
                .endDate(now.plusDays(4))
                .build());


        List<Booking> bookings = bookingRepository.getCurrentBookingByBooker(booker1.getId(), Pageable.unpaged());
        assertThat(bookings)
                .hasSize(1)
                .containsAll(List.of(currentBookingByBooker1));
    }

    @Test
    void getFutureBookingByBooker_shouldInvokeRepositoryAndReturnTheSame() {
        LocalDateTime now = LocalDateTime.now();

        Booking pastBookingByBooker1 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(WAITING)
                .item(item1)
                .startDate(now.minusDays(2))
                .endDate(now.minusDays(1))
                .build());

        Booking currentBookingByBooker1 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item1)
                .startDate(now.minusDays(1))
                .endDate(now.plusDays(1))
                .build());

        Booking futureBookingByBooker1 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item1)
                .startDate(now.plusDays(1))
                .endDate(now.plusDays(2))
                .build());

        Booking bookingByBooker2 = bookingRepository.save(Booking.builder()
                .booker(booker2)
                .status(APPROVED)
                .item(item1)
                .startDate(now.plusDays(3))
                .endDate(now.plusDays(4))
                .build());

        List<Booking> bookings = bookingRepository.getFutureBookingByBooker(booker1.getId(), Pageable.unpaged());
        assertThat(bookings)
                .hasSize(1)
                .containsAll(List.of(futureBookingByBooker1));
    }

    @Test
    void getBookingByOwner_shouldInvokeRepositoryAndReturnTheSame() {
        LocalDateTime now = LocalDateTime.now();

        Booking bookingByOwner1 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item1)
                .startDate(now.plusDays(1))
                .endDate(now.plusDays(2))
                .build());

        Booking bookingByOwner2 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item2)
                .startDate(now.plusDays(3))
                .endDate(now.plusDays(4))
                .build());

        List<Booking> bookings = bookingRepository.getBookingByOwner(owner1.getId(), Pageable.unpaged());
        assertThat(bookings)
                .hasSize(1)
                .containsAll(List.of(bookingByOwner1));
    }

    @Test
    void getBookingByOwnerAndStatus_shouldInvokeRepositoryAndReturnTheSame() {
        LocalDateTime now = LocalDateTime.now();

        Booking bookingByOwner1WithWaitingStatus = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(WAITING)
                .item(item1)
                .startDate(now.plusDays(1))
                .endDate(now.plusDays(2))
                .build());

        Booking bookingByOwner1WithApprovedStatus = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item1)
                .startDate(now.plusDays(1))
                .endDate(now.plusDays(2))
                .build());

        Booking bookingByOwner2 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item2)
                .startDate(now.plusDays(3))
                .endDate(now.plusDays(4))
                .build());

        List<Booking> bookings = bookingRepository.getBookingByOwnerAndStatus(owner1.getId(), APPROVED, Pageable.unpaged());
        assertThat(bookings)
                .hasSize(1)
                .containsAll(List.of(bookingByOwner1WithApprovedStatus));
    }

    @Test
    void getPastBookingByOwner_shouldInvokeRepositoryAndReturnTheSame() {
        LocalDateTime now = LocalDateTime.now();

        Booking pastBookingByBooker1 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(WAITING)
                .item(item1)
                .startDate(now.minusDays(2))
                .endDate(now.minusDays(1))
                .build());

        Booking currentBookingByBooker1 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item1)
                .startDate(now.minusDays(1))
                .endDate(now.plusDays(1))
                .build());

        Booking futureBookingByBooker1 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item1)
                .startDate(now.plusDays(1))
                .endDate(now.plusDays(2))
                .build());

        Booking bookingByBooker2 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item2)
                .startDate(now.plusDays(3))
                .endDate(now.plusDays(4))
                .build());


        List<Booking> bookings = bookingRepository.getPastBookingByOwner(owner1.getId(), Pageable.unpaged());
        assertThat(bookings)
                .hasSize(1)
                .containsAll(List.of(pastBookingByBooker1));
    }

    @Test
    void getCurrentBookingByOwner_shouldInvokeRepositoryAndReturnTheSame() {
        LocalDateTime now = LocalDateTime.now();

        Booking pastBookingByOwner1 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(WAITING)
                .item(item1)
                .startDate(now.minusDays(2))
                .endDate(now.minusDays(1))
                .build());

        Booking currentBookingByOwner1 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item1)
                .startDate(now.minusDays(1))
                .endDate(now.plusDays(1))
                .build());

        Booking futureBookingByOwner1 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item1)
                .startDate(now.plusDays(1))
                .endDate(now.plusDays(2))
                .build());

        Booking bookingByOwner2 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item2)
                .startDate(now.plusDays(3))
                .endDate(now.plusDays(4))
                .build());


        List<Booking> bookings = bookingRepository.getCurrentBookingByOwner(owner1.getId(), Pageable.unpaged());
        assertThat(bookings)
                .hasSize(1)
                .containsAll(List.of(currentBookingByOwner1));
    }

    @Test
    void getFutureBookingByOwner_shouldInvokeRepositoryAndReturnTheSame() {
        LocalDateTime now = LocalDateTime.now();

        Booking pastBookingByOwner1 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(WAITING)
                .item(item1)
                .startDate(now.minusDays(2))
                .endDate(now.minusDays(1))
                .build());

        Booking currentBookingByOwner1 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item1)
                .startDate(now.minusDays(1))
                .endDate(now.plusDays(1))
                .build());

        Booking futureBookingByOwner1 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item1)
                .startDate(now.plusDays(1))
                .endDate(now.plusDays(2))
                .build());

        Booking bookingByOwner2 = bookingRepository.save(Booking.builder()
                .booker(booker1)
                .status(APPROVED)
                .item(item2)
                .startDate(now.plusDays(3))
                .endDate(now.plusDays(4))
                .build());

        List<Booking> bookings = bookingRepository.getFutureBookingByOwner(owner1.getId(), Pageable.unpaged());
        assertThat(bookings)
                .hasSize(1)
                .containsAll(List.of(futureBookingByOwner1));
    }
}
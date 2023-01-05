package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoDate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = ShareItApp.class)
public class IntgrServiceTest {
    private final EntityManager entityManager;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final User user1 = new User();
    private final User user2 = new User();
    private final Item item1 = new Item();
    private final BookingDto booking1 = new BookingDto();
    private final BookingDto booking2 = new BookingDto();
    private final CommentDto commentDto = new CommentDto();

    @BeforeEach
    public void init() {
        entityManager.createNativeQuery("set referential_integrity false;").executeUpdate();
        entityManager.createNativeQuery("truncate table items restart identity;").executeUpdate();
        entityManager.createNativeQuery("truncate table users restart identity;").executeUpdate();
        entityManager.createNativeQuery("truncate table bookings restart identity;").executeUpdate();
        entityManager.createNativeQuery("truncate table requests restart identity;").executeUpdate();
        entityManager.createNativeQuery("truncate table comments restart identity;").executeUpdate();
        entityManager.createNativeQuery("set referential_integrity true;").executeUpdate();

        item1.setId(1);
        item1.setAvailable(true);
        item1.setName("item1");
        item1.setDescription("descr of item1");
        item1.setOwner(1);

        user1.setId(1);
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        user2.setId(2);
        user2.setName("user2");
        user2.setEmail("u2@user.com");

        booking1.setId(1);
        booking1.setStart(LocalDateTime.now().plusDays(1).withNano(0));
        booking1.setEnd(LocalDateTime.now().plusDays(2).withNano(0));
        booking1.setItemId(1);
        booking1.setUserId(2);
        booking1.setStatus(Status.APPROVED);

        booking2.setId(2);
        booking2.setStart(LocalDateTime.now().plusDays(3).withNano(0));
        booking2.setEnd(LocalDateTime.now().plusDays(4).withNano(0));
        booking2.setItemId(1);
        booking2.setUserId(2);
        booking2.setStatus(Status.APPROVED);

        commentDto.setAuthor(2);
        commentDto.setItem(1);
        commentDto.setText("comment about item1");
    }

    @Test
    public void addItemTest() {
        userService.add(user1);
        itemService.add(item1);
        TypedQuery<Item> query = entityManager.createQuery("select i from Item i where i.name = :name", Item.class);
        Item itemResult = query.setParameter("name", item1.getName()).getSingleResult();
        Assertions.assertEquals(item1.getId(), itemResult.getId());
        Assertions.assertEquals(item1.getName(), itemResult.getName());
        Assertions.assertEquals(item1.getDescription(), itemResult.getDescription());
        Assertions.assertEquals(item1.getAvailable(), itemResult.getAvailable());
        Assertions.assertEquals(item1.getOwner(), itemResult.getOwner());
    }

    @Test
    public void addItemWithAbsencedUserTest() {
        Assertions.assertThrows(ResponseStatusException.class, () -> itemService.add(item1));
    }

    @Test
    public void updateItemTest() {
        userService.add(user1);
        itemService.add(item1);
        Item item = new Item();
        item.setId(1);
        item.setName("updName");
        item.setDescription("updDesc");
        item.setOwner(1);
        item.setAvailable(true);
        itemService.update(item, user1.getId());
        TypedQuery<Item> query = entityManager.createQuery("select i from Item i where i.name = :name", Item.class);
        Item itemResult = query.setParameter("name", item.getName()).getSingleResult();
        Assertions.assertEquals(item.getId(), itemResult.getId());
        Assertions.assertEquals(item.getName(), itemResult.getName());
        Assertions.assertEquals(item.getDescription(), itemResult.getDescription());
        Assertions.assertEquals(item.getAvailable(), itemResult.getAvailable());
        Assertions.assertEquals(item.getOwner(), itemResult.getOwner());
    }

    @Test
    public void updateItemWithInvalidOwnerTest() {
        userService.add(user1);
        userService.add(user2);
        itemService.add(item1);
        Item item = new Item();
        item.setId(1);
        item.setName("updName");
        item.setDescription("updDesc");
        item.setOwner(2);
        item.setAvailable(true);
        Assertions.assertThrows(ResponseStatusException.class, () -> itemService.update(item, user2.getId()));
    }

    @Test
    public void findByIdItemTest() {
        userService.add(user1);
        itemService.add(item1);
        Item itemResult = itemService.getById(item1.getId());
        Assertions.assertEquals(item1.getId(), itemResult.getId());
        Assertions.assertEquals(item1.getAvailable(), itemResult.getAvailable());
        Assertions.assertEquals(item1.getName(), itemResult.getName());
        Assertions.assertEquals(item1.getDescription(), itemResult.getDescription());
        Assertions.assertEquals(item1.getOwner(), itemResult.getOwner());
    }

    @Test
    public void getItemByNameOrDescriptionTest1() {
        Item item2 = new Item();
        item2.setId(2);
        item2.setName("item2");
        item2.setDescription("Descr Item2");
        item2.setOwner(1);
        item2.setAvailable(true);
        userService.add(user1);
        itemService.add(item1);
        itemService.add(item2);
        Collection<Item> items = itemService.getByNameOrDesc("item", 0, 10);
        Assertions.assertEquals(2, items.size());
    }

    @Test
    public void getItemByNameOrDescriptionTest2() {
        Item item2 = new Item();
        item2.setId(2);
        item2.setName("item2");
        item2.setDescription("Descr Item2");
        item2.setOwner(1);
        item2.setAvailable(true);
        userService.add(user1);
        itemService.add(item1);
        itemService.add(item2);
        Collection<Item> items = itemService.getByNameOrDesc("DeSc", 0, 10);
        Assertions.assertEquals(2, items.size());
    }

    @Test
    public void getAllItemsByOwnerTest() {
        Item item2 = new Item();
        item2.setId(2);
        item2.setName("item2");
        item2.setDescription("Descr Item2");
        item2.setOwner(1);
        item2.setAvailable(true);
        userService.add(user1);
        itemService.add(item1);
        itemService.add(item2);
        List<ItemDtoDate> itemDtoDates = (List<ItemDtoDate>) itemService.getAll(1, 0, 10);
        Assertions.assertEquals(2, itemDtoDates.size());
        Assertions.assertEquals(item1.getId(), itemDtoDates.get(0).getId());
        Assertions.assertEquals(item2.getId(), itemDtoDates.get(1).getId());
        Assertions.assertEquals(item1.getAvailable(), itemDtoDates.get(0).getAvailable());
        Assertions.assertEquals(item2.getAvailable(), itemDtoDates.get(1).getAvailable());

    }

    @Test
    public void addCommentTest() {
        userService.add(user1);
        userService.add(user2);
        itemService.add(item1);
        bookingService.add(booking1, 2);
        bookingService.updBookingDate(1, LocalDateTime.now().minusDays(5));
        CommentDto commentDtoResult = itemService.addComment(commentDto, 1, 2);
        Assertions.assertEquals(1, commentDtoResult.getId());
    }

    @Test
    public void getItemWithDateBookingTest() {
        userService.add(user1);
        userService.add(user2);
        itemService.add(item1);
        bookingService.add(booking1, 2);
        bookingService.add(booking2, 2);
        bookingService.updBookingDate(1, LocalDateTime.now().minusDays(20));
        bookingService.updBookingDate(2, LocalDateTime.now().minusDays(10));

        itemService.addComment(commentDto, 1, 2);
        ItemDtoDate itemDtoDate = itemService.getItemDate(1, LocalDateTime.now().minusDays(15), 2);
        Assertions.assertEquals(1, itemDtoDate.getId());
        Assertions.assertEquals(item1.getName(), itemDtoDate.getName());
        Assertions.assertEquals(item1.getDescription(), itemDtoDate.getDescription());
        Assertions.assertEquals(item1.getAvailable(), itemDtoDate.getAvailable());
        Assertions.assertEquals(item1.getOwner(), itemDtoDate.getOwner());
    }

    @Test
    public void getUserTest() {
        userService.add(user1);
        User userResult = userService.getById(user1.getId());
        Assertions.assertEquals(user1, userResult);
    }

    @Test
    public void deleteItemTest() {
        userService.add(user1);
        itemService.add(item1);
        Assertions.assertEquals(itemService.getById(1), item1);
        itemService.delete(1, 1);
        Assertions.assertThrows(ResponseStatusException.class, () -> itemService.getById(1));
    }

}

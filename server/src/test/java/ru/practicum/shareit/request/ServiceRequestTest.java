package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Collection;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = ShareItApp.class)
public class ServiceRequestTest {

    private final UserService userService;
    private final ItemRequestService itemRequestService;

    private final ItemRepository itemRepository;

    private final EntityManager entityManager;

    private final User user1 = new User();
    private final User user2 = new User();
    private final Item item1 = new Item();
    private final Item item2 = new Item();
    private final ItemRequest request1 = new ItemRequest();
    private final ItemRequest request2 = new ItemRequest();

    @BeforeEach
    public void init() {
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE;").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE table items restart identity;").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE table users restart identity;").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE table requests restart identity;").executeUpdate();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE;").executeUpdate();

        user1.setId(1);
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        user2.setId(2);
        user2.setName("user2");
        user2.setEmail("u2@user.com");
        item1.setId(1);
        item1.setName("item1");
        item1.setDescription("descr item1");
        item1.setAvailable(true);
        item1.setOwner(user1.getId());
        item1.setRequestId(1);
        item2.setId(2);
        item2.setName("item2");
        item2.setDescription("descr item2");
        item2.setAvailable(true);
        item2.setOwner(user1.getId());
        item2.setRequestId(1);
        request1.setId(1);
        request1.setDescription("descr about the item1");
        request1.setRequestor(user1);
        request1.setCreated(LocalDateTime.now().withNano(0));
        request2.setId(2);
        request2.setDescription("descr about the item2");
        request2.setRequestor(user1);
        request2.setCreated(LocalDateTime.now().withNano(0));

    }

    @Test
    public void addRequestTest() {
        userService.add(user1);
        ItemRequest itemRequestResult = itemRequestService.add(request1);
        Assertions.assertEquals(request1, itemRequestResult);
    }

    @Test
    public void addRequestWithInvalidUserTest() {
        userService.add(user1);
        request1.setRequestor(user2);
        Assertions.assertThrows(ResponseStatusException.class, () -> itemRequestService.add(request1));
    }

    @Test
    public void getAllOwnRequestsTest() {
        userService.add(user1);
        itemRequestService.add(request1);
        itemRequestService.add(request2);
        Collection<ItemRequest> collection = itemRequestService.getAllOwn(user1.getId());
        Assertions.assertEquals(2, collection.size());
        Assertions.assertEquals(request1, collection.toArray()[0]);
        Assertions.assertEquals(request2, collection.toArray()[1]);
    }

    @Test
    public void getByIdRequestTest() {
        userService.add(user1);
        itemRequestService.add(request1);
        ItemRequest itemRequestResult = itemRequestService.getById(1, 1);
        Assertions.assertEquals(1, itemRequestResult.getId());
        Assertions.assertEquals(request1, itemRequestResult);
    }

    @Test
    public void getAllRequestsTest() {
        userService.add(user1);
        userService.add(user2);
        itemRequestService.add(request1);
        itemRequestService.add(request2);
        Collection<ItemRequest> collection = itemRequestService.getAll(2, 0, 10);
        Assertions.assertEquals(2, collection.size());
        Assertions.assertEquals(request1, collection.toArray()[0]);
        Assertions.assertEquals(request2, collection.toArray()[1]);
    }

    @Test
    public void saveItemTest() {
        userService.add(user1);
        Item itemResult = itemRepository.save(item1);
        Assertions.assertEquals(item1, itemResult);
    }

}

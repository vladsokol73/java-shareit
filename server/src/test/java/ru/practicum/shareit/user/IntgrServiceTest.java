package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collection;

@Transactional
@SpringBootTest(properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = ShareItApp.class)
public class IntgrServiceTest {
    private final EntityManager entityManager;
    private final UserService userService;
    private final User user1 = new User();
    private final User user2 = new User();

    @BeforeEach
    public void init() {
        entityManager.createNativeQuery("set referential_integrity false;").executeUpdate();
        entityManager.createNativeQuery("truncate table users restart identity;").executeUpdate();
        entityManager.createNativeQuery("set referential_integrity true;").executeUpdate();
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        user2.setName("user2");
        user2.setEmail("u2@user.com");
    }

    @Test
    public void addUserTest() {
        userService.add(user1);
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.name = :name", User.class);
        User userResult = query.setParameter("name", user1.getName()).getSingleResult();
        Assertions.assertEquals(user1.getName(), userResult.getName());
        Assertions.assertEquals(user1.getEmail(), userResult.getEmail());
        Assertions.assertEquals(1, userResult.getId());
    }

    @Test
    public void updateUserTest() {
        userService.add(user1);
        user2.setName("UpdUser");
        user2.setEmail("upd@user.com");
        userService.update(user2, 1);
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.id = :id", User.class);
        User userResult = query.setParameter("id", 1).getSingleResult();
        Assertions.assertEquals(user2.getName(), userResult.getName());
        Assertions.assertEquals(user2.getEmail(), userResult.getEmail());
        Assertions.assertEquals(1, userResult.getId());
    }

    @Test
    public void getUserByIdTest() {
        userService.add(user1);
        User userResult = userService.getById(1);
        Assertions.assertEquals(1, userResult.getId());
        Assertions.assertEquals(user1.getName(), userResult.getName());
        Assertions.assertEquals(user1.getEmail(), userResult.getEmail());
    }

    @Test
    public void getAllUsersTest() {
        userService.add(user1);
        userService.add(user2);
        Collection<User> users = userService.getAll();
        Assertions.assertEquals(2, users.size());
        Assertions.assertTrue(users.contains(user1));
        Assertions.assertTrue(users.contains(user2));
    }

    @Test
    public void deleteUserTest() {
        userService.add(user1);
        Collection<User> users = userService.getAll();
        Assertions.assertEquals(users.size(), 1);
        userService.delete(1);
        users = userService.getAll();
        Assertions.assertEquals(0, users.size());
    }

    @Test
    public void deleteAllUsers() {
        userService.add(user1);
        userService.add(user2);
        Collection<User> users = userService.getAll();
        Assertions.assertEquals(users.size(), 2);
        userService.deleteAll();
        users = userService.getAll();
        Assertions.assertEquals(0, users.size());
    }
}

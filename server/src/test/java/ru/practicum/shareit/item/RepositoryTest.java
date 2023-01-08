package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.TypedQuery;
import java.util.Collection;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = ShareItApp.class)
public class RepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    private final Item item1 = new Item();
    private final User user1 = new User();

    private final ItemRepository itemRepository;

    @Test
    public void findByNameOrDescriptionItemTest1() {
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        entityManager.persist(user1);
        item1.setName("item1");
        item1.setDescription("desc Item1");
        item1.setAvailable(true);
        item1.setOwner(1);
        entityManager.persist(item1);
        TypedQuery<Item> query = entityManager.getEntityManager()
                .createQuery("select i from Item i where upper(i.name) like upper(concat('%',:str,'%')) or "
                        + "upper(i.description) like upper(concat('%',:str,'%')) ", Item.class);
        Item itemResult = query.setParameter("str", "item").getSingleResult();
        Assertions.assertEquals(item1.getName(), itemResult.getName());

    }

    @Test
    public void findByNameOrDescriptionItemTest2() {
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        entityManager.persist(user1);
        item1.setName("item1");
        item1.setDescription("desc Item1");
        item1.setAvailable(true);
        item1.setOwner(1);
        entityManager.persist(item1);
        TypedQuery<Item> query = entityManager.getEntityManager()
                .createQuery("select i from Item i where upper(i.name) like upper(concat('%',:str,'%')) or "
                        + "upper(i.description) like upper(concat('%',:str,'%')) ", Item.class);
        Item itemResult = query.setParameter("str", "Desc ITEM").getSingleResult();
        Assertions.assertEquals(item1.getDescription(), itemResult.getDescription());

    }

    @Test
    public void findByNameOrDescriptionItemTest3() {
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        entityManager.persist(user1);
        item1.setName("item1");
        item1.setDescription("desc Item1");
        item1.setAvailable(true);
        item1.setOwner(1);
        entityManager.persist(item1);

        Pageable pageable = PageRequest.of(0, 10);
        Collection<Item> items = itemRepository.findByNameOrDesc("item", pageable).getContent();
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(item1, items.toArray()[0]);
    }
}

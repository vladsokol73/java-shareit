package ru.practicum.shareit.item.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryTest {


    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    User owner1, owner2;


    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();

        owner1 = userRepository.save(User.builder()
                .name("owner1")
                .email("owner1@mail.ru")
                .build());

        owner2 = userRepository.save(User.builder()
                .name("owner2")
                .email("owner2@mail.ru")
                .build());
    }

    @Test
    void findByOwner_shouldInvokeRepositoryAndReturnTheSame() {
        Item item1 = itemRepository.save(Item.builder()
                .name("item1")
                .description("description1")
                .available(true)
                .owner(owner1)
                .build());

        Item item2 = itemRepository.save(Item.builder()
                .name("item2")
                .description("description2")
                .available(true)
                .owner(owner2)
                .build());

        List<Item> items = itemRepository.findByOwner(owner1.getId());

        assertThat(items)
                .hasSize(1)
                .containsAll(List.of(item1));
    }

    @Test
    void findAvailableItemsByNameOrDescription_shouldInvokeRepositoryAndReturnTheSame() {
        String commonPart = "common";

        Item item1 = itemRepository.save(Item.builder()
                .name("item1")
                .description(commonPart + "erd")
                .available(true)
                .owner(owner1)
                .build());

        Item item2 = itemRepository.save(Item.builder()
                .name("item2")
                .description("sjd" + commonPart + "erd")
                .available(false)
                .owner(owner1)
                .build());

        Item item3 = itemRepository.save(Item.builder()
                .name("item3")
                .description("jjl" + commonPart)
                .available(true)
                .owner(owner1)
                .build());

        List<Item> items = itemRepository.findAvailableItemsByNameOrDescription(commonPart);

        assertThat(items)
                .hasSize(2)
                .containsAll(List.of(item1, item3));
    }
}
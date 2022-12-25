package ru.practicum.shareit.item.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.item.ItemBaseTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ItemFactoryTest extends ItemBaseTest {

    @Autowired
    ItemFactory itemFactory;

    @MockBean
    ItemRepository itemRepository;

    @Test
    void get_shouldInvokeServiceAndReturnTheSame() {
        int id = 1;
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));

        Item item = itemFactory.get(id);
        assertThat(item.getId()).isEqualTo(id);
    }

    @Test
    void get_shouldThrowNotFoundExceptionWhenRepositoryReturnEmpty() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemFactory.get(1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Item with ID 1 not found");
    }
}
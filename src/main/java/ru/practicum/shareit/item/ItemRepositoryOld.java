package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepositoryOld {
    Item add(Item item);

    Item update(Item item, Integer id);

    Item getById(Integer id);

    Collection<Item> getByNameOrDesc(String text);

    Collection<Item> getAll(Integer userId);

    void delete(Integer itemId, Integer userId);
}

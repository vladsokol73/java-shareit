package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOutAbs;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOutAbs;

import java.util.List;

public interface ItemService {

    ItemDtoOutAbs create(ItemDtoIn item, int ownerId);

    CommentDtoOutAbs create(CommentDtoIn commentDtoIn, int itemId, int userId);

    ItemDtoOutAbs getById(int id, int userId);

    List<? extends ItemDtoOutAbs> getAvailableItemByOwner(int userId);

    List<? extends ItemDtoOutAbs> getAvailableItemByPattern(String pattern);

    ItemDtoOutAbs update(int id, ItemDtoIn itemDto, int userId);

    void delete(int id);
}

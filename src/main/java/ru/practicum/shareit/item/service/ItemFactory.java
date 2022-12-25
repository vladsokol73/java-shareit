package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.util.exception.NotFoundException;

@Component
@RequiredArgsConstructor
public class ItemFactory {

    private final ItemRepository itemRepository;

    public Item get(int id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with ID " + id + " not found"));
    }
}


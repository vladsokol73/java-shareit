package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.util.exception.NotFoundException;

@Component
@RequiredArgsConstructor
public class ItemRequestFactory {

    private final ItemRequestRepository itemRequestRepository;

    public ItemRequest get(int requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));
    }
}


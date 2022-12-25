package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutAbs;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.OffsetLimitPageable;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository repository;

    private final UserService userService;

    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDtoOutAbs create(ItemRequestDtoIn itemRequestDto, int userId) {

        ItemRequest itemRequest = itemRequestMapper.fromDto(itemRequestDto, userId);

        ItemRequest savedItemRequest = repository.save(itemRequest);
        log.info("{} is saved", savedItemRequest);

        return itemRequestMapper.toDto(savedItemRequest);
    }

    @Transactional
    @Override
    public ItemRequestDtoOutAbs getById(int id, int userId) {
        userService.existenceCheck(userId);

        ItemRequest itemRequest = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(""));

        int forLoading = itemRequest.getItems().size();

        return itemRequestMapper.toDto(itemRequest);
    }

    @Transactional
    @Override
    public List<? extends ItemRequestDtoOutAbs> getAllByRequestor(int requestorId) {
        userService.existenceCheck(requestorId);

        List<ItemRequest> itemRequests = repository.getItemRequestByRequestor(requestorId);
        int forLoading;
        for (ItemRequest itemRequest : itemRequests)
            forLoading = itemRequest.getItems().size();

        log.info("Found {} item requests", itemRequests.size());

        return itemRequestMapper.toDto(itemRequests);
    }

    @Transactional
    @Override
    public List<? extends ItemRequestDtoOutAbs> getAll(int userId, int from, int size) {
        List<ItemRequest> itemRequests = repository.findAllByRequestorIdNot(
                userId,
                new OffsetLimitPageable(from, size, Sort.by(Sort.Direction.ASC, "created"))
        ).getContent();

        int forLoading;
        for (ItemRequest itemRequest : itemRequests)
            forLoading = itemRequest.getItems().size();

        log.info("Found {} item requests by the requestor with ID {}. From {}, size {}", itemRequests.size(), userId, from, size);

        return itemRequestMapper.toDto(itemRequests);
    }
}

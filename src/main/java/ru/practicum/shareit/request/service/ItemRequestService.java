package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutAbs;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDtoOutAbs create(ItemRequestDtoIn itemRequestDto, int userId);

    ItemRequestDtoOutAbs getById(int id, int userId);

    List<? extends ItemRequestDtoOutAbs> getAllByRequestor(int requestorId);

    List<? extends ItemRequestDtoOutAbs> getAll(int requestorId, int from, int size);
}

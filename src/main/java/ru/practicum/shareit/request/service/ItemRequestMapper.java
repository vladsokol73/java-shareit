package ru.practicum.shareit.request.service;

import org.mapstruct.*;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserFactory;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, UserFactory.class})
public interface ItemRequestMapper {

    ItemRequestDtoOut toDto(ItemRequest itemRequest);

    List<ItemRequestDtoOut> toDto(List<ItemRequest> itemRequests);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(source = "requesterId", target = "requestor")
    ItemRequest fromDto(ItemRequestDtoIn itemRequestDto, Integer requesterId);
}

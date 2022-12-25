package ru.practicum.shareit.item.service;

import org.mapstruct.*;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.FullItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.service.ItemRequestFactory;
import ru.practicum.shareit.user.service.UserFactory;

import java.util.List;


@Mapper(componentModel = "spring",
        uses = {UserFactory.class, BookingMapper.class, CommentMapper.class, ItemFactory.class, ItemRequestFactory.class})
public interface ItemMapper {

    @Mapping(source = "request.id", target = "requestId")
    ItemDtoOut toDto(Item item);

    List<ItemDtoOut> toDto(List<Item> items);

    @Mapping(source = "request.id", target = "requestId")
    FullItemDtoOut toFullDto(Item item);

    List<FullItemDtoOut> toFullDto(List<Item> items);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(source = "itemDtoIn.requestId", target = "request")
    @Mapping(source = "ownerId", target = "owner")
    Item fromDto(ItemDtoIn itemDtoIn, Integer ownerId);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItemFromDto(ItemDtoIn itemDtoIn, @MappingTarget Item item);
}
package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestBaseTest {

    protected User owner, requester;

    protected Item item;
    protected ItemRequest itemRequest;
    protected ItemDtoOut itemDtoOut;

    protected ItemRequestDtoIn itemRequestDtoIn;
    protected ItemRequestDtoOut itemRequestDtoOut;

    protected LocalDateTime now;

    @BeforeEach
    protected void setUp() {
        now = LocalDateTime.now().withNano(0);

        owner = new User(1, "owner", "owner@gmail.com");
        item = Item.builder()
                .id(1)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        requester = new User(2, "requester", "requester@gmail.com");
        itemRequest = ItemRequest.builder()
                .id(1)
                .description("description")
                .requestor(requester)
                .created(now)
                .items(List.of(item))
                .build();
        item.setRequest(itemRequest);

        itemDtoOut = ItemDtoOut.builder()
                .id(1)
                .name(item.getName())
                .description(item.getDescription())
                .available(true)
                .build();

        itemRequestDtoOut = ItemRequestDtoOut.builder()
                .id(1)
                .description(itemRequest.getDescription())
                .created(now)
                .items(List.of(itemDtoOut))
                .build();

        itemDtoOut.setRequestId(itemRequestDtoOut.getId());

        itemRequestDtoIn = new ItemRequestDtoIn(itemRequest.getDescription());
    }
}

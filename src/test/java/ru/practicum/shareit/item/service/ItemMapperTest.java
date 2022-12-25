package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.item.ItemBaseTest;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.service.ItemRequestFactory;
import ru.practicum.shareit.user.service.UserFactory;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ItemMapperTest extends ItemBaseTest {

    @Autowired
    private ItemMapper mapper;

    @MockBean
    private UserFactory userFactory;

    @MockBean
    private ItemRequestFactory itemRequestFactory;

    @Test
    void toDto_shouldInvokeServiceAndReturnTheSame() {
        assertThat(mapper.toDto(item)).isEqualTo(itemDtoOut);
    }

    @Test
    void toDto_shouldInvokeServiceAndReturnNull() {
        Item item = null;
        assertThat(mapper.toDto(item)).isNull();
    }

    @Test
    void toDto_shouldInvokeServiceAndReturnTheSameWithoutItemRequestId() {
        item.getRequest().setId(null);
        itemDtoOut.setRequestId(null);
        assertThat(mapper.toDto(item)).isEqualTo(itemDtoOut);
    }

    @Test
    void toDtoList_shouldInvokeServiceAndReturnTheSame() {
        assertThat(mapper.toDto(List.of(item))).isEqualTo(List.of(itemDtoOut));
    }

    @Test
    void toDtoList_shouldInvokeServiceAndReturnNull() {
        List<Item> items = null;
        assertThat(mapper.toDto(items)).isNull();
    }

    @Test
    void toFullDto_shouldInvokeServiceAndReturnTheSame() {
        assertThat(mapper.toFullDto(item)).isEqualTo(fullItemDtoOut);
    }

    @Test
    void toFullDto_shouldInvokeServiceAndReturnNull() {
        Item item = null;
        assertThat(mapper.toFullDto(item)).isNull();
    }

    @Test
    void toFullDtoList_shouldInvokeServiceAndReturnTheSame() {
        assertThat(mapper.toFullDto(List.of(item))).isEqualTo(List.of(fullItemDtoOut));
    }

    @Test
    void toFullDtoList_shouldInvokeServiceAndReturnNull() {
        List<Item> items = null;
        assertThat(mapper.toFullDto(items)).isNull();
    }

    @Test
    void fromDto_shouldInvokeServiceAndReturnTheSame() {
        when(userFactory.get(anyInt())).thenReturn(owner);
        when(itemRequestFactory.get(anyInt())).thenReturn(itemRequest);

        assertThat(mapper.fromDto(itemDtoIn, owner.getId())).isEqualTo(item);
    }

    @Test
    void fromDto_shouldInvokeServiceAndReturnNull() {
        assertThat(mapper.fromDto(null, null)).isNull();
    }


    @Test
    void updateItemFromDto_shouldInvokeServiceAndReturnTheSame() {
        ItemDtoIn itemDto = ItemDtoIn.builder()
                .name("newItem")
                .description("newDescription")
                .available(false)
                .build();

        Item expected = Item.builder()
                .id(item.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(item.getOwner())
                .request(item.getRequest())
                .build();

        mapper.updateItemFromDto(itemDto, item);

        assertThat(item).isEqualTo(expected);
    }

    @Test
    void updateItemFromDto_shouldInvokeServiceAndReturnUpdatedName() {
        ItemDtoIn itemDto = ItemDtoIn.builder()
                .name("newItem")
                .build();

        Item expected = Item.builder()
                .id(item.getId())
                .name(itemDto.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .request(item.getRequest())
                .build();

        mapper.updateItemFromDto(itemDto, item);

        assertThat(item).isEqualTo(expected);
    }

   @Test
    void updateItemFromDto_shouldInvokeServiceAndReturnUpdatedDescription() {
       ItemDtoIn itemDto = ItemDtoIn.builder()
                .description("newDescription")
                .build();

        Item expected = Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(itemDto.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .request(item.getRequest())
                .build();

        mapper.updateItemFromDto(itemDto, item);

        assertThat(item).isEqualTo(expected);
    }

    @Test
    void updateItemFromDto_shouldInvokeServiceAndReturnUpdatedAvailable() {
        ItemDtoIn itemDto = ItemDtoIn.builder()
                .available(false)
                .build();

        Item expected = Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(itemDto.getAvailable())
                .owner(item.getOwner())
                .request(item.getRequest())
                .build();

        mapper.updateItemFromDto(itemDto, item);

        assertThat(item).isEqualTo(expected);
    }
}
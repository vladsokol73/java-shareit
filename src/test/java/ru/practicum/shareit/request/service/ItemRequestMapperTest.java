package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.request.ItemRequestBaseTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserFactory;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ItemRequestMapperTest extends ItemRequestBaseTest {

    @Autowired
    ItemRequestMapper mapper;

    @MockBean
    UserFactory userFactory;

    @Test
    void toDto_shouldInvokeServiceAndReturnTheSame() {
        assertThat(mapper.toDto(itemRequest)).isEqualTo(itemRequestDtoOut);
    }

    @Test
    void toDto_shouldInvokeServiceAndReturnNull() {
        ItemRequest itemRequest = null;
        assertThat(mapper.toDto(itemRequest)).isNull();
    }

    @Test
    void toDtoList_shouldInvokeServiceAndReturnTheSame() {
        assertThat(mapper.toDto(Arrays.asList(itemRequest))).isEqualTo(Arrays.asList(itemRequestDtoOut));
    }

    @Test
    void toDtoList_shouldInvokeServiceAndReturnNull() {
        List<ItemRequest> requests = null;
        assertThat(mapper.toDto(requests)).isNull();
    }

   @Test
    void fromDto_shouldInvokeServiceAndReturnTheSame() {
        when(userFactory.get(anyInt())).thenReturn(requester);
        assertThat(mapper.fromDto(itemRequestDtoIn, 1)).isEqualTo(itemRequest);
    }
}

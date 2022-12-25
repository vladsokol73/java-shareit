package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.request.ItemRequestBaseTest;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutAbs;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.OffsetLimitPageable;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ItemRequestServiceTest extends ItemRequestBaseTest {

    @MockBean
    ItemRequestRepository itemRequestRepository;
    @MockBean
    UserService userService;

    @Autowired
    ItemRequestService itemRequestService;

    @MockBean
    ItemRequestMapper itemRequestMapper;

    @BeforeEach
    protected void setUp() {
        super.setUp();

        doNothing().when(userService).existenceCheck(anyInt());

        when(itemRequestMapper.toDto(any(ItemRequest.class))).thenReturn(itemRequestDtoOut);
        when(itemRequestMapper.toDto(anyList())).thenReturn(List.of(itemRequestDtoOut));
        when(itemRequestMapper.fromDto(any(ItemRequestDtoIn.class), anyInt())).thenReturn(itemRequest);
    }

    @Test
    void create_shouldInvokeRepositoryAndReturnTheSame() {
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);

        ItemRequestDtoOutAbs savedItemRequest = itemRequestService.create(itemRequestDtoIn, 1);

        assertThat(savedItemRequest).isEqualTo(itemRequestDtoOut);

        verify(itemRequestRepository, times(1)).save(itemRequest);
    }

    @Test
    void getById_shouldThrowNotFoundExceptionWhenRepositoryReturnsEmpty() {
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> {
            itemRequestService.getById(1, 1);
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getById_shouldInvokeRepositoryAndReturnTheSame() {
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.of(itemRequest));

        ItemRequestDtoOutAbs itemRequestById = itemRequestService.getById(1, 1);

        verify(itemRequestRepository, times(1)).findById(1);
        assertThat(itemRequestById).isEqualTo(itemRequestDtoOut);
    }

    @Test
    void getAllByRequestor_shouldInvokeRepositoryAndReturnTheSame() {
        when(itemRequestRepository.getItemRequestByRequestor(anyInt()))
                .thenReturn(Arrays.asList(itemRequest));

        List<? extends ItemRequestDtoOutAbs> itemRequests = itemRequestService.getAllByRequestor(1);

        verify(itemRequestRepository, times(1)).getItemRequestByRequestor(eq(1));
        assertThat(itemRequests.get(0)).isEqualTo(itemRequestDtoOut);
    }

    @Test
    void getAll_shouldInvokeRepositoryAndReturnTheSame() {
        when(itemRequestRepository.findAllByRequestorIdNot(anyInt(), any(OffsetLimitPageable.class)))
                .thenReturn(new PageImpl(Arrays.asList(itemRequest)));

        List<? extends ItemRequestDtoOutAbs> itemRequests = itemRequestService.getAll(1, 0, 10);

        verify(itemRequestRepository, times(1)).findAllByRequestorIdNot(eq(1), any(OffsetLimitPageable.class));
        assertThat(itemRequests.get(0)).isEqualTo(itemRequestDtoOut);
    }

}

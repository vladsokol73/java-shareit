package ru.practicum.shareit.item.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.item.ItemBaseTest;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.exception.ActionIsNotAvailableException;
import ru.practicum.shareit.util.exception.ForbiddenException;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.model.BookingStatus.*;

@SpringBootTest
public class ItemServiceTest extends ItemBaseTest {

    @Autowired
    ItemService itemService;

    @MockBean
    ItemRepository itemRepo;

    @MockBean
    CommentRepository commentRepo;

    @MockBean
    UserService userService;

    @MockBean
    ItemMapper itemMapper;

    @MockBean
    CommentMapper commentMapper;

    @BeforeEach
    protected void setUp() {
        super.setUp();

        doNothing().when(userService).existenceCheck(anyInt());

        when(itemMapper.toDto(any(Item.class))).thenReturn(itemDtoOut);
        when(itemMapper.toDto(anyList())).thenReturn(List.of(itemDtoOut));
        when(itemMapper.toFullDto(any(Item.class))).thenReturn(fullItemDtoOut);
        when(itemMapper.toFullDto(anyList())).thenReturn(List.of(fullItemDtoOut));
        when(itemMapper.fromDto(any(ItemDtoIn.class), anyInt())).thenReturn(item);

        when(commentMapper.toDto(any(Comment.class)))
                .thenReturn(commentDtoOut);
        when(commentMapper.fromDto(any(CommentDtoIn.class), anyInt(), anyInt()))
                .thenReturn(comment);
    }

    @Test
    void create_shouldInvokeRepositoryAndReturnTheSame() {
        doNothing().when(userService)
                .existenceCheck(anyInt());

        when(itemRepo.save(item))
                .thenReturn(item);

        ItemDtoOutAbs savedItem = itemService.create(itemDtoIn, 1);

        assertThat(savedItem)
                .isEqualTo(itemDtoOut);

        verify(itemRepo, times(1)).save(item);
    }

    @Test
    void getById_shouldThrowNotFoundExceptionWhenRepositoryReturnEmpty() {
        when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getById(1, 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Item with ID 1 is not found");

        verify(itemRepo, times(1)).findById(1);
    }

    @Test
    void getById_shouldInvokeRepositoryAndReturnTheSame() {
        when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.of(item));


        ItemDtoOutAbs itemById = itemService.getById(1, item.getOwner().getId());

        assertThat(itemById).isEqualTo(fullItemDtoOut);
        verify(itemRepo, times(1)).findById(1);
    }

    @Test
    void getAvailableItemByOwner_shouldInvokeRepositoryAndReturnTheSameFullItemDtoOut() {
        when(itemRepo.findByOwner(anyInt()))
                .thenReturn(Collections.singletonList(item));

        List<? extends ItemDtoOutAbs> items = itemService.getAvailableItemByOwner(item.getOwner().getId());

        assertThat(items.get(0)).isEqualTo(fullItemDtoOut);

        verify(itemRepo, times(1)).findByOwner(item.getOwner().getId());
    }

    @Test
    void getAvailableItemByOwner_shouldInvokeRepositoryAndReturnTheSameItemDtoOut() {
        when(itemRepo.findByOwner(anyInt()))
                .thenReturn(Collections.singletonList(item));

        int userId = 3;
        assertThat(item.getOwner().getId()).isNotEqualTo(userId);

        List<? extends ItemDtoOutAbs> items = itemService.getAvailableItemByOwner(userId);

        assertThat(items).isEqualTo(List.of(itemDtoOut));

        verify(itemRepo, times(1)).findByOwner(userId);
    }

    @Test
    void getAvailableItemByOwner_shouldInvokeRepositoryAndReturnEmptyDto() {
        when(itemRepo.findByOwner(anyInt()))
                .thenReturn(Collections.emptyList());

        List<? extends ItemDtoOutAbs> items = itemService.getAvailableItemByOwner(1);

        assertThat(items).isEqualTo(List.of(itemDtoOut));

        verify(itemRepo, times(1)).findByOwner(1);
    }

    @Test
    void getAvailableItemByPattern_shouldInvokeRepositoryAndReturnTheSame() {
        when(itemRepo.findAvailableItemsByNameOrDescription(anyString()))
                .thenReturn(Collections.singletonList(item));

        List<? extends ItemDtoOutAbs> items = itemService.getAvailableItemByPattern("desc");

        assertThat(items).isEqualTo(List.of(itemDtoOut));

        verify(itemRepo, times(1)).findAvailableItemsByNameOrDescription("desc");
    }

    @Test
    void getAvailableItemByPattern_shouldGetEmptyItemWhenPatternIsNull() {
        assertThat(itemService.getAvailableItemByPattern(null))
                .isEmpty();

        verify(itemRepo, never()).findAvailableItemsByNameOrDescription(null);
    }

    @Test
    void getAvailableItemByPattern_shouldGetEmptyItemWhenPatternIsEmpty() {
        assertThat(itemService.getAvailableItemByPattern(""))
                .isEmpty();

        verify(itemRepo, never()).findAvailableItemsByNameOrDescription("");
    }

    @Test
    void getAvailableItemByPattern_shouldGetEmptyItemWhenPatternIsBlank() {
        assertThat(itemService.getAvailableItemByPattern(" "))
                .isEmpty();

        verify(itemRepo, never()).findAvailableItemsByNameOrDescription(" ");
    }

    @Test
    void update_shouldThrowExceptionWhenUserIsNotOwner() {
        when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.update(1, any(ItemDtoIn.class), 1))
                .isInstanceOf(ForbiddenException.class);

        verify(itemRepo, times(1)).findById(1);
    }

    @Test
    void update_shouldGetFromRepositoryAndPatchAndSaveAndReturnSaved() {
        when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.of(item));
        doNothing().when(itemMapper)
                .updateItemFromDto(any(ItemDtoIn.class), any(Item.class));

        ItemDtoOutAbs updatedItem = itemService.update(1, itemDtoIn, 2);

        assertThat(updatedItem).isEqualTo(itemDtoOut);

        verify(itemRepo, times(1)).findById(1);
    }

    @Test
    void delete_shouldInvokeRepositoryDelete() {
        when(itemRepo.existsById(anyInt()))
                .thenReturn(true);

        doNothing().when(itemRepo)
                .deleteById(anyInt());

        itemService.delete(1);
        verify(itemRepo, times(1)).deleteById(1);
    }

    @Test
    void delete_shouldThrowNotFoundExceptionWhenInvokeRepositoryWithWrongId() {
        when(itemRepo.existsById(anyInt()))
                .thenReturn(false);

        assertThatThrownBy(() -> itemService.delete(1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Item with ID 1 is not found");
    }

    @Test
    void createComment_shouldThrowExceptionWhenBookingIsCanceled() {
        booking.setStatus(CANCELED);
        booking.setEndDate(LocalDateTime.now().minusDays(1));
        assertThatThrownBy(() -> {
            itemService.create(commentDtoIn, item.getId(), booking.getBooker().getId());
        }).isInstanceOf(ActionIsNotAvailableException.class);
    }

    @Test
    void createComment_shouldThrowExceptionWhenBookingIsWaiting() {
        booking.setStatus(WAITING);
        booking.setEndDate(LocalDateTime.now().minusDays(1));
        assertThatThrownBy(() -> {
            itemService.create(commentDtoIn, item.getId(), comment.getAuthor().getId());
        }).isInstanceOf(ActionIsNotAvailableException.class);
    }

    @Test
    void createComment_shouldThrowExceptionWhenBookingIsRejected() {
        booking.setStatus(REJECTED);
        booking.setEndDate(LocalDateTime.now().minusDays(1));
        assertThatThrownBy(() -> {
            itemService.create(commentDtoIn, item.getId(), booking.getBooker().getId());
        }).isInstanceOf(ActionIsNotAvailableException.class);
    }

    @Test
    void createComment_shouldThrowExceptionWhenUserWasNotBooker() {
        booking.setStatus(APPROVED);
        booking.setEndDate(LocalDateTime.now().minusDays(1));
        final int USER_ID = 3;

        comment.setAuthor(User.builder().id(USER_ID).name("user").email("user@gmail.com").build());
        assertThat(USER_ID).isNotEqualTo(booking.getBooker().getId());

        assertThatThrownBy(() -> {
            itemService.create(commentDtoIn, item.getId(), USER_ID);
        }).isInstanceOf(ActionIsNotAvailableException.class);
    }

    @Test
    void createComment_shouldThrowExceptionWhenBookingIsNotFinished() {
        booking.setStatus(APPROVED);
        booking.setEndDate(LocalDateTime.now().plusDays(1));

        assertThatThrownBy(() -> {
            itemService.create(commentDtoIn, item.getId(), booking.getBooker().getId());
        }).isInstanceOf(ActionIsNotAvailableException.class);
    }

    @Test
    void createComment_shouldInvokeRepositoryAndReturnTheSame() {
        booking.setStatus(APPROVED);
        booking.setEndDate(now.minusDays(1));

        comment.setAuthor(booker);

        when(commentRepo.save(comment)).thenReturn(comment);

        CommentDtoOutAbs savedComment = itemService.create(commentDtoIn, item.getId(), booking.getBooker().getId());

        verify(commentRepo, times(1)).save(comment);

        assertThat(savedComment).isEqualTo(commentDtoOut);
    }
}
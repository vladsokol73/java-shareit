package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOutAbs;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOutAbs;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.exception.ActionIsNotAvailableException;
import ru.practicum.shareit.util.exception.ForbiddenException;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static ru.practicum.shareit.booking.service.BookingServiceImpl.getLastBooking;
import static ru.practicum.shareit.booking.service.BookingServiceImpl.getNextBooking;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepo;

    private final CommentRepository commentRepo;

    private final UserService userService;

    private final ItemMapper itemMapper;

    private final CommentMapper commentMapper;

    @Override
    public ItemDtoOutAbs create(ItemDtoIn itemDtoIn, int ownerId) {
        Item item = itemMapper.fromDto(itemDtoIn, ownerId);

        userService.existenceCheck(item.getOwner().getId());

        Item savedItem = itemRepo.save(item);

        log.info("{} is saved", savedItem);

        return itemMapper.toDto(savedItem);
    }

    @Transactional
    @Override
    public ItemDtoOutAbs getById(int id, int userId) {
        Item item = itemRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with ID " + id + " is not found"));

        int forLoading = item.getBookings().size();

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            item.setLastBooking(getLastBooking(item, now));
            item.setNextBooking(getNextBooking(item, now));

            forLoading = item.getComments().size();

            log.info("{} is found", item);
        }

        return itemMapper.toFullDto(item);
    }

    @Override
    public List<? extends ItemDtoOutAbs> getAvailableItemByOwner(int ownerId) {
        List<Item> items = itemRepo.findByOwner(ownerId);
        if (items.isEmpty())
            return itemMapper.toDto(items);

        if (items.get(0).getOwner().getId().equals(ownerId)) {
            LocalDateTime now = LocalDateTime.now();
            items.forEach(item -> {
                item.setLastBooking(getLastBooking(item, now));
                item.setNextBooking(getNextBooking(item, now));
            });

            log.info("Founded {} items by owner with ID {}", items.size(), ownerId);
            return itemMapper.toFullDto(items);
        }

        return itemMapper.toDto(items);
    }

    @Override
    public List<? extends ItemDtoOutAbs> getAvailableItemByPattern(String pattern) {
        if (pattern == null || pattern.isBlank() || pattern.isEmpty())
            return Collections.emptyList();

        List<Item> items = itemRepo.findAvailableItemsByNameOrDescription(pattern);
        log.info("Founded {} items by pattern {}", items.size(), pattern);
        return itemMapper.toDto(items);
    }

    @Override
    @Transactional
    public ItemDtoOutAbs update(int id, ItemDtoIn itemDto, int userId) {
        Item item = itemRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with ID " + id + " is not found"));

        if (item.getOwner().getId() != userId) {
            log.info("User with ID {} cannot change {}. Only owner can do it", userId, itemDto);
            throw new ForbiddenException();
        }

        itemMapper.updateItemFromDto(itemDto, item);

        log.info("{} is updated", item);

        return itemMapper.toDto(item);
    }

    @Override
    public void delete(int id) {
        if (!itemRepo.existsById(id)) {
            throw new NotFoundException("Item with ID " + id + " is not found");
        }

        itemRepo.deleteById(id);
        log.info("Item with ID {} is removed", id);
    }

    @Override
    public CommentDtoOutAbs create(CommentDtoIn commentDtoIn, int itemId, int userId) {
        Comment comment = commentMapper.fromDto(commentDtoIn, itemId, userId);

        bookingCheck(comment.getItem(), comment.getAuthor().getId());

        Comment savedComment = commentRepo.save(comment);
        log.info("{} is saved", savedComment);

        return commentMapper.toDto(savedComment);
    }

    private void bookingCheck(Item item, int authorId) {
        if (item.getBookings() == null || item.getBookings().stream().noneMatch(booking ->
                booking.getBooker().getId().equals(authorId) &&
                        booking.getStatus().equals(BookingStatus.APPROVED) &&
                        booking.getEndDate().isBefore(LocalDateTime.now()))) {
            log.warn("User with ID {} did not book {}", authorId, item);
            throw new ActionIsNotAvailableException();
        }
    }
}

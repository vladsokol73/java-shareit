package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOutAbs;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOutAbs;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.validator.NullAllowed;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;

import static ru.practicum.shareit.user.controller.UserController.USER_ID_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDtoOutAbs create(@RequestHeader(value = USER_ID_HEADER) int ownerId,
                                @Valid @RequestBody ItemDtoIn itemDto,
                                BindingResult result) {
        if (result.getErrorCount() != 0) {
            log.error("Validation errors: {}", result.getAllErrors());
            throw new ValidationException();
        }

        return itemService.create(itemDto, ownerId);
    }

    @GetMapping("/{id}")
    public ItemDtoOutAbs getById(@RequestHeader(value = USER_ID_HEADER) int ownerId,
                                 @PathVariable("id") int id) {
        return itemService.getById(id, ownerId);
    }

    @GetMapping
    public List<? extends ItemDtoOutAbs> getAll(@RequestHeader(value = USER_ID_HEADER) int userId) {
        return itemService.getAvailableItemByOwner(userId);
    }

    @GetMapping("/search")
    public List<? extends ItemDtoOutAbs> search(@RequestParam("text") String pattern) {
        return itemService.getAvailableItemByPattern(pattern);
    }

    @PatchMapping("/{id}")
    public ItemDtoOutAbs update(@PathVariable("id") int id,
                                @RequestHeader(value = USER_ID_HEADER) Integer userId,
                                @Validated(NullAllowed.class) @RequestBody ItemDtoIn itemDto) {
        return itemService.update(id, itemDto, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int id) {
        itemService.delete(id);
    }


    @PostMapping("/{id}/comment")
    public CommentDtoOutAbs create(@PathVariable("id") int itemId,
                                   @RequestHeader(value = USER_ID_HEADER) int userId,
                                   @Valid @RequestBody CommentDtoIn commentDtoIn,
                                   BindingResult result) {
        if (result.getErrorCount() != 0) {
            log.error("Validation errors: {}", result.getAllErrors());
            throw new ValidationException();
        }

        return itemService.create(commentDtoIn, itemId, userId);
    }
}

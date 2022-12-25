package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOutAbs;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.validator.NullAllowed;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final UserService userService;

    @PostMapping
    public UserDtoOutAbs create(@Valid @RequestBody UserDtoIn userDtoIn,
                                BindingResult result) {
        if (result.getErrorCount() != 0) {
            log.error("Validation errors: {}", result.getAllErrors());
            throw new ValidationException();
        }

        return userService.create(userDtoIn);
    }

    @GetMapping("/{id}")
    public UserDtoOutAbs getById(@PathVariable("id") int id) {
        return userService.getById(id);
    }

    @GetMapping
    public List<? extends UserDtoOutAbs> getAll() {
        return userService.getAll();
    }

    @PatchMapping("/{id}")
    public UserDtoOutAbs update(@Validated(NullAllowed.class) @RequestBody UserDtoIn userDtoIn,
                                @PathVariable("id") int id) {
        return userService.update(id, userDtoIn);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int id) {
        userService.delete(id);
    }
}

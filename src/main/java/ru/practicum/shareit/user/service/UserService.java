package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;

import java.util.List;

public interface UserService {

    UserDtoOut create(UserDtoIn userDto);

    UserDtoOut getById(int id);

    List<UserDtoOut> getAll();

    void existenceCheck(int id);

    UserDtoOut update(int id, UserDtoIn user);

    void delete(int id);
}

package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.ConflictException;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repo;

    private final UserMapper mapper;

    @Override
    public UserDtoOut create(UserDtoIn userDto) {
        User user = mapper.fromDto(userDto);

        User savedUser;
        try {
            savedUser = repo.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Email already in use");
        }

        log.info("{} is saved", savedUser);

        return mapper.toDto(savedUser);
    }

    @Override
    public void existenceCheck(int id) {
        if (!repo.existsById(id)) {
            log.warn("User with ID {} doesn't exist", id);
            throw new NotFoundException("User with ID " + id + " doesn't exist");
        }
    }

    @Override
    public UserDtoOut getById(int id) {
        User user = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("User with ID " + id + " is not found"));

        log.info("{} is found", user);

        return mapper.toDto(user);
    }

    @Override
    public List<UserDtoOut> getAll() {
        List<User> users = repo.findAll();
        log.info("{} users are founded", users.size());
        return mapper.toDto(users);
    }

    @Override
    public UserDtoOut update(int id, UserDtoIn userDtoIn) {
        User user = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("User with ID " + id + " is not found"));

        mapper.updateUserFromDto(userDtoIn, user);

        try {
            repo.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Email already in use");
        }

        log.info("{} is updated", user);
        return mapper.toDto(user);
    }

    @Override
    public void delete(int id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("User with ID " + id + " is not found");
        }

        repo.deleteById(id);
        log.info("User with ID {} is removed", id);
    }
}

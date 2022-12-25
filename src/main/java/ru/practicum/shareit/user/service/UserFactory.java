package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.NotFoundException;

@Component
@RequiredArgsConstructor
public class UserFactory {

    private final UserRepository repo;

    public User get(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
    }
}

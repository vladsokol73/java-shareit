package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserRepositoryOld {
    User getById(Integer id);

    Collection<User> getAll();

    User add(User user);

    User update(User user, Integer id);

    void delete(Integer id);

    void deleteAll();

    Boolean isPresent(Integer id);
}

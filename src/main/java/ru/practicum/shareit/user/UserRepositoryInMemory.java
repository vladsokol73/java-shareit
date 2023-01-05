package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class UserRepositoryInMemory implements UserRepositoryOld {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer idUser = 0;

    private Integer getNewId() {
        return ++idUser;
    }

    private void validateOnUpdate(Integer id) {
        if (users.get(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private void validateOnEmailConflict(User user) {
        for (User userTest : users.values()) {
            if (user.getEmail().equals(userTest.getEmail())) {
                log.info(">>>>conflict>>>> user:{}===userTest:{}", user.toString(), userTest.toString());
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
        }
    }

    @Override
    public User getById(Integer id) {
        log.info("запрошен пользователь id={}, user=/{}", id, users.get(id).toString());
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        log.info("запрошены все пользователи");
        return users.values();
    }

    @Override
    public User add(User user) {
        validateOnEmailConflict(user);
        user.setId(getNewId());
        users.put(user.getId(), user);
        log.info("добавлен пользователь id={}", user.getId());
        return user;
    }

    @Override
    public User update(User user, Integer id) {
        validateOnUpdate(id);
        if (user.getEmail() != null) {
            validateOnEmailConflict(user);
            users.get(id).setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            users.get(id).setName(user.getName());
        }

        log.info("обновлен пользователь id={}, user= /{}", id, users.get(id).toString());
        return users.get(id);
    }

    @Override
    public void delete(Integer id) {
        if (users.get(id) != null) {
            users.remove(id);
            log.info("удален пользователь id={}", id);
        }
    }

    @Override
    public void deleteAll() {
        users.clear();
        log.info("удалены все пользователи");
    }

    @Override
    public Boolean isPresent(Integer id) {
        if (users.get(id) == null) {
            return false;
        } else {
            return true;
        }

    }
}

package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface UserRepository extends JpaRepository<User, Integer> {
    public boolean existsByEmail(String email);
}

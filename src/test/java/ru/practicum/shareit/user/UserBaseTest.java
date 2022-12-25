package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.model.User;

public class UserBaseTest {

    protected User user;
    protected UserDtoIn userDtoIn;
    protected UserDtoOut userDtoOut;

    @BeforeEach
    protected void setUp() {
        user = User.builder()
                .id(1)
                .name("user")
                .email("user@gmail.com")
                .build();

        userDtoIn = UserDtoIn.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();

        userDtoOut = UserDtoOut.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

}

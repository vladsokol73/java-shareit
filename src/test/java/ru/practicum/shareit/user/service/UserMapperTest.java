package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.UserBaseTest;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class UserMapperTest extends UserBaseTest {

    @Autowired
    private UserMapper mapper;

    @Test
    void toDto_shouldInvokeServiceAndReturnTheSame() {
        assertThat(mapper.toDto(user)).isEqualTo(userDtoOut);
    }

    @Test
    void toDto_shouldInvokeServiceAndReturnNull() {
        User user = null;
        assertThat(mapper.toDto(user)).isNull();
    }

    @Test
    void toDtoList_shouldInvokeServiceAndReturnTheSame() {
        assertThat(mapper.toDto(List.of(user))).isEqualTo(List.of(userDtoOut));
    }

    @Test
    void toDtoList_shouldInvokeServiceAndReturnNull() {
        List<User> users = null;
        assertThat(mapper.toDto(users)).isNull();
    }

    @Test
    void fromDto_shouldInvokeServiceAndReturnTheSame() {
        assertThat(mapper.fromDto(userDtoIn)).isEqualTo(user);
    }

    @Test
    void fromDto_shouldInvokeServiceAndReturnNull() {
        UserDtoIn userDto = null;
        assertThat(mapper.fromDto(userDto)).isNull();
    }

    @Test
    void updateUserFromDto_shouldInvokeServiceAndReturnUserWithUpdatedName() {
        UserDtoIn userDto = UserDtoIn.builder()
                .name("newName")
                .build();

        User expected = User.builder()
                .id(user.getId())
                .name(userDto.getName())
                .email(user.getEmail())
                .build();

        mapper.updateUserFromDto(userDto, user);

        assertThat(user).isEqualTo(expected);
    }

    @Test
    void updateUserFromDto_shouldInvokeServiceAndReturnUserWithUpdatedEmail() {
        UserDtoIn userDto = UserDtoIn.builder()
                .email("newEmail@gmail.com")
                .build();

        User expected = User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(userDto.getEmail())
                .build();

        mapper.updateUserFromDto(userDto, user);

        assertThat(user).isEqualTo(expected);
    }
}

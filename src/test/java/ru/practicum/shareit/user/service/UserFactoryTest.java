package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.UserBaseTest;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserFactoryTest extends UserBaseTest {

    @Autowired
    private UserFactory userFactory;

    @MockBean
    private UserRepository userRepository;

    @Test
    void get_shouldInvokeRepositoryAndReturnTheSame() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));

        assertThat(userFactory.get(1)).isEqualTo(user);

        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void get_shouldThrowNotFoundExceptionWhenRepositoryReturnEmpty() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userFactory.get(1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with ID 1 not found");
    }
}
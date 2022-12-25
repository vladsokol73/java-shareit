package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.user.UserBaseTest;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.ConflictException;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest extends UserBaseTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserMapper userMapper;

    @BeforeEach
    protected void setUp() {
        super.setUp();

        when(userMapper.fromDto(any(UserDtoIn.class)))
                .thenReturn(user);

        when(userMapper.toDto(any(User.class)))
                .thenReturn(userDtoOut);

        when(userMapper.toDto(anyList()))
                .thenReturn(List.of(userDtoOut));
    }

    @Test
    void create_shouldInvokeRepositoryAndReturnTheSame() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDtoOut savedUser = userService.create(userDtoIn);

        verify(userRepository, times(1)).save(user);

        assertThat(savedUser).isEqualTo(userDtoOut);
    }

    @Test
    void create_shouldThrowConflictExceptionWithDuplicateEmail() {
        when(userRepository.save(any(User.class)))
                .thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> userService.create(userDtoIn))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Email already in use");

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void existenceCheck_shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.existsById(anyInt()))
                .thenReturn(false);

        assertThatThrownBy(() -> userService.existenceCheck(1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with ID 1 doesn't exist");

        verify(userRepository, times(1)).existsById(1);
    }

    @Test
    void existenceCheck_shouldInvokeRepository() {
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);

        userService.existenceCheck(1);

        verify(userRepository, times(1)).existsById(1);
    }

    @Test
    void getById_shouldThrowNotFoundExceptionWhenRepositoryReturnEmpty() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with ID 1 is not found");

        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void getById_shouldInvokeRepositoryAndReturnTheSame() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        UserDtoOut userById = userService.getById(1);

        assertThat(userById).isEqualTo(userDtoOut);

        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void getAll_shouldInvokeRepositoryAndReturnTheSame() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDtoOut> users = userService.getAll();

        assertThat(users).isNotNull().isNotEmpty().containsAll(List.of(userDtoOut));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void update_shouldGetFromRepositoryAndPatchAndSaveAndReturnSaved() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        doNothing().when(userMapper)
                .updateUserFromDto(any(UserDtoIn.class), any(User.class));

        UserDtoOut updatedUser = userService.update(1, userDtoIn);

        assertThat(updatedUser).isEqualTo(userDtoOut);

        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void update_shouldThrowNotFoundExceptionWhenRepositoryReturnEmpty() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(1, userDtoIn))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with ID 1 is not found");

        verify(userRepository, times(1)).findById(1);

        verify(userRepository, never()).save(user);
    }

    @Test
    void update_shouldThrowConflictExceptionWithDuplicateEmail() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any()))
                .thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> userService.update(1, userDtoIn))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Email already in use");

        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void delete_shouldInvokeRepositoryDelete() {
        doNothing().when(userRepository)
                .deleteById(anyInt());

        when(userRepository.existsById(anyInt()))
                .thenReturn(true);

        userService.delete(1);

        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void delete_shouldThrowNotFoundExceptionWhenInvokeRepositoryWithWrongId() {
        when(userRepository.existsById(anyInt()))
                .thenReturn(false);

        assertThatThrownBy(() -> userService.delete(1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with ID 1 is not found");
    }
}
package ru.practicum.shareit.request.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.request.ItemRequestBaseTest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ItemRequestFactoryTest extends ItemRequestBaseTest {

    @Autowired
    ItemRequestFactory wrapper;

    @MockBean
    ItemRequestRepository itemRequestRepository;

    @Test
    void loadTest() {
        assertThat(wrapper).isNotNull();
    }

    @Test
    void get_shouldInvokeServiceAndReturnTheSame() {
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(itemRequest));

        assertThat(wrapper.get(1)).isEqualTo(itemRequest);
    }

    @Test
    void get_shouldThrowException() {
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> {
            wrapper.get(1);
        }).isInstanceOf(NotFoundException.class)
                .hasMessage("Request not found");
    }
}
package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository requestRepository;

    User requestor1, requestor2;


    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();

        requestor1 = userRepository.save(User.builder()
                .name("requestor1")
                .email("requestor1@mail.ru")
                .build());

        requestor2 = userRepository.save(User.builder()
                .name("requestor2")
                .email("requestor2@mail.ru")
                .build());
    }

    @Test
    void getItemRequestByRequestor_shouldInvokeRepositoryAndReturnTheSame() {
        ItemRequest request1 = requestRepository.save(ItemRequest.builder()
                .description("description1")
                .requestor(requestor1)
                .created(LocalDateTime.now())
                .build());

        ItemRequest request2 = requestRepository.save(ItemRequest.builder()
                .description("description2")
                .requestor(requestor2)
                .created(LocalDateTime.now())
                .build());

        List<ItemRequest> items = requestRepository.getItemRequestByRequestor(request1.getId());

        assertThat(items).hasSize(1)
                .containsAll(List.of(request1));
    }

    @Test
    void findAll_shouldInvokeRepositoryAndReturnTheSame() {
        ItemRequest request1 = requestRepository.save(ItemRequest.builder()
                .description("description1")
                .requestor(requestor1)
                .created(LocalDateTime.now())
                .build());

        ItemRequest request2 = requestRepository.save(ItemRequest.builder()
                .description("description2")
                .requestor(requestor2)
                .created(LocalDateTime.now())
                .build());

        Collection<ItemRequest> items = requestRepository.findAll();

        assertThat(items).hasSize(2)
                .containsAll(List.of(request1, request2));
    }

    @Test
    void findAllByRequestorIdNot_shouldInvokeRepositoryAndReturnTheSame() {
        ItemRequest request1 = requestRepository.save(ItemRequest.builder()
                .description("description1")
                .requestor(requestor1)
                .created(LocalDateTime.now())
                .build());

        ItemRequest request2 = requestRepository.save(ItemRequest.builder()
                .description("description2")
                .requestor(requestor2)
                .created(LocalDateTime.now())
                .build());

        List<ItemRequest> items = requestRepository.findAllByRequestorIdNot(request1.getId(), Pageable.unpaged()).getContent();

        assertThat(items).hasSize(1)
                .containsAll(List.of(request2));
    }
}

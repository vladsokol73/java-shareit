package ru.practicum.shareit.item.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.item.ItemBaseTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.service.UserFactory;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CommentMapperTest extends ItemBaseTest {

    @Autowired
    CommentMapper mapper;

    @MockBean
    ItemFactory itemFactory;

    @MockBean
    UserFactory userFactory;

    @Test
    void toDto_shouldInvokeServiceAndReturnTheSame() {
        assertThat(mapper.toDto(comment)).isEqualTo(commentDtoOut);
    }

    @Test
    void toDto_shouldInvokeServiceAndReturnNull() {
        Comment comment = null;
        assertThat(mapper.toDto(comment)).isNull();
    }

    @Test
    void toDtoList_shouldInvokeServiceAndReturnTheSame() {
        assertThat(mapper.toDto(List.of(comment))).isEqualTo(List.of(commentDtoOut));
    }

    @Test
    void toDtoList_shouldInvokeServiceAndReturnNull() {
        List<Comment> comment = null;
        assertThat(mapper.toDto(comment)).isNull();
    }

    @Test
    void fromDto_shouldInvokeServiceAndReturnTheSame() {
        when(itemFactory.get(anyInt())).thenReturn(item);
        when(userFactory.get(anyInt())).thenReturn(commentator);

        assertThat(mapper.fromDto(commentDtoIn, 1, 1)).isEqualTo(comment);
    }

    @Test
    void fromDto_shouldInvokeServiceAndReturnNull() {
        assertThat(mapper.fromDto(null, null, null)).isNull();
    }
}

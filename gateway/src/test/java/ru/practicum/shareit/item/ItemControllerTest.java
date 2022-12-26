package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.comment.CommentDtoFromRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemClient client;
    @InjectMocks
    private ItemController controller;
    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    public void test1_tryCreateNotValidItem() throws Exception {
        ItemDto emptyName = new ItemDto(1L, "", "description", true);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(emptyName))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is(400));
    }

    @Test
    public void test2_tryCreateNotValidItem() throws Exception {
        ItemDto nullDescription = new ItemDto();
        nullDescription.setId(2L);
        nullDescription.setName("Name");
        nullDescription.setAvailable(false);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(nullDescription))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is(400));
    }

    @Test
    public void test3_tryCreateNotValidItem() throws Exception {
        ItemDto nullAvailable = new ItemDto();
        nullAvailable.setId(3L);
        nullAvailable.setName("Name");
        nullAvailable.setDescription("Description");

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(nullAvailable))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is(400));
    }

    @Test
    public void test4_tryCreateCommentWithIncorrectText() throws Exception {
        CommentDtoFromRequest commentBody = new CommentDtoFromRequest();
        mvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(commentBody))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is(400));
    }

    @Test
    public void test5_tryCreateCommentWithIncorrectText() throws Exception {
        CommentDtoFromRequest commentBody = new CommentDtoFromRequest();
        commentBody.setText("");
        mvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(commentBody))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is(400));
    }
}
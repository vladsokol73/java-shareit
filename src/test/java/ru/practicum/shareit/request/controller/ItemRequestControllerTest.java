package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.ItemRequestBaseTest;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.ErrorHandler;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.user.controller.UserController.USER_ID_HEADER;

@SpringBootTest
public class ItemRequestControllerTest extends ItemRequestBaseTest {

    @Autowired
    private ItemRequestController controller;

    @MockBean
    private ItemRequestService itemRequestService;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    @BeforeEach
    protected void setUp() {
        super.setUp();

        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ErrorHandler.class)
                .build();
    }

    @SneakyThrows
    @Test
    void create_shouldAnswerOK() {
        when(itemRequestService.create(any(ItemRequestDtoIn.class), anyInt()))
                .thenReturn(itemRequestDtoOut);

        mvc.perform(post("/requests")
                        .header(USER_ID_HEADER, "1")
                        .content(mapper.writeValueAsString(itemRequestDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(result -> System.out.println("response = " + result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(itemRequest.getId())))
                .andExpect(jsonPath("$.description", Matchers.is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.created", Matchers.is(itemRequest.getCreated().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))));
    }

    @SneakyThrows
    @Test
    void create_shouldReturnBadRequestWhenDescriptionIsNull() {
        itemRequestDtoIn.setDescription(null);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void create_shouldReturnBadRequestWhenDescriptionIsBlank() {
        itemRequestDtoIn.setDescription(" ");

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void create_shouldReturnBadRequestWhenDescriptionIsEmpty() {
        itemRequestDtoIn.setDescription("");

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @SneakyThrows
    @Test
    void getById_shouldAnswerOK() {
        when(itemRequestService.getById(anyInt(), anyInt()))
                .thenReturn(itemRequestDtoOut);

        mvc.perform(get("/requests/1")
                        .header(USER_ID_HEADER, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(result -> System.out.println("response = " + result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(itemRequest.getId())))
                .andExpect(jsonPath("$.description", Matchers.is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.created", Matchers.is(itemRequest.getCreated().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))));
    }

    @SneakyThrows
    @Test
    void getAllByRequestor_shouldAnswerOK() {
        doReturn(List.of(itemRequestDtoOut)).when(itemRequestService)
                .getAllByRequestor(anyInt());

        mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(itemRequest.getId())))
                .andExpect(jsonPath("$[0].description", Matchers.is(itemRequest.getDescription())))
                .andExpect(jsonPath("$[0].created", Matchers.is(itemRequest.getCreated().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))));
    }

    @SneakyThrows
    @Test
    void getAll_shouldAnswerOK() {
        doReturn(List.of(itemRequestDtoOut)).when(itemRequestService)
                .getAll(anyInt(), anyInt(), anyInt());

        mvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, "1")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(itemRequest.getId())))
                .andExpect(jsonPath("$[0].description", Matchers.is(itemRequest.getDescription())))
                .andExpect(jsonPath("$[0].created", Matchers.is(itemRequest.getCreated().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))));
    }

}

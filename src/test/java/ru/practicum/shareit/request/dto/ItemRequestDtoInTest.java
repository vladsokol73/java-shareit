package ru.practicum.shareit.request.dto;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.ItemRequestBaseTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoInTest extends ItemRequestBaseTest {

    @Autowired
    private JacksonTester<ItemRequestDtoIn> jacksonTester;

    @Test
    void toItemRequestWithAnswersDto() throws IOException {
        JsonContent<ItemRequestDtoIn> result = jacksonTester.write(itemRequestDtoIn);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDtoIn.getDescription());
    }
}
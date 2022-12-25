package ru.practicum.shareit.item.dto;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.ItemBaseTest;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentDtoInTest extends ItemBaseTest {

    @Autowired
    private JacksonTester<CommentDtoIn> jacksonTester;

    @Test
    void toItemDto() throws IOException {
        JsonContent<CommentDtoIn> result = jacksonTester.write(commentDtoIn);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(commentDtoIn.getText());
    }
}
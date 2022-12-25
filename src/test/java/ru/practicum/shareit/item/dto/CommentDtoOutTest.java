package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.ItemBaseTest;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentDtoOutTest extends ItemBaseTest {

    @Autowired
    private JacksonTester<CommentDtoOut> jacksonTester;

    @Test
    void toItemDto() throws IOException {
        JsonContent<CommentDtoOut> result = jacksonTester.write(commentDtoOut);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(commentDtoOut.getId());
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(commentDtoOut.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(commentDtoOut.getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(now
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }
}

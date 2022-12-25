package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.ItemRequestBaseTest;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoOutTest extends ItemRequestBaseTest {

    @Autowired
    private JacksonTester<ItemRequestDtoOut> jacksonTester;

    @Test
    void toItemRequestWithAnswersDto() throws IOException {
        JsonContent<ItemRequestDtoOut> result = jacksonTester.write(itemRequestDtoOut);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequestDtoOut.getId());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDtoOut.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(itemRequestDtoOut.getCreated()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathArrayValue("$.items").extracting("id").contains(
                itemDtoOut.getId());
        assertThat(result).extractingJsonPathArrayValue("$.items").extracting("name").contains(
                itemDtoOut.getName());
        assertThat(result).extractingJsonPathArrayValue("$.items").extracting("description").contains(
                itemDtoOut.getDescription());
        assertThat(result).extractingJsonPathArrayValue("$.items").extracting("available").contains(
                itemDtoOut.getAvailable());
    }
}

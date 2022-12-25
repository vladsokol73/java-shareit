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
public class ItemDtoOutTest extends ItemBaseTest {

    @Autowired
    private JacksonTester<ItemDtoOut> jacksonTester;

    @Test
    void toItemDto() throws IOException {
        JsonContent<ItemDtoOut> result = jacksonTester.write(itemDtoOut);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDtoOut.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDtoOut.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDtoOut.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDtoOut.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(itemDtoOut.getRequestId());
    }
}

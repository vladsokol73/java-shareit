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
public class ItemDtoInTest extends ItemBaseTest {

    @Autowired
    private JacksonTester<ItemDtoIn> jacksonTester;

    @Test
    void toItemDto() throws IOException {
        JsonContent<ItemDtoIn> result = jacksonTester.write(itemDtoIn);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDtoIn.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDtoIn.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDtoIn.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(itemDtoIn.getRequestId());
    }

}

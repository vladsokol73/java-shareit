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
public class FullItemDtoOutTest extends ItemBaseTest {

    @Autowired
    private JacksonTester<FullItemDtoOut> jacksonTester;

    @Test
    void toItemDto() throws IOException {
        JsonContent<FullItemDtoOut> result = jacksonTester.write(fullItemDtoOut);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(fullItemDtoOut.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(fullItemDtoOut.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(fullItemDtoOut.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(fullItemDtoOut.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(fullItemDtoOut.getRequestId());
        assertThat(result).extractingJsonPathArrayValue("$.comments").extracting("id")
                .contains(fullItemDtoOut.getComments().get(0).getId());
        assertThat(result).extractingJsonPathArrayValue("$.comments").extracting("text")
                .contains(fullItemDtoOut.getComments().get(0).getText());
        assertThat(result).extractingJsonPathArrayValue("$.comments").extracting("authorName")
                .contains(fullItemDtoOut.getComments().get(0).getAuthorName());
        assertThat(result).extractingJsonPathArrayValue("$.comments").extracting("created")
                .contains(fullItemDtoOut.getComments().get(0).getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo(fullItemDtoOut.getNextBooking().getId());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId")
                .isEqualTo(fullItemDtoOut.getNextBooking().getBookerId());
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start")
                .isEqualTo(fullItemDtoOut.getNextBooking().getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end")
                .isEqualTo(fullItemDtoOut.getNextBooking().getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(fullItemDtoOut.getLastBooking().getId());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo(fullItemDtoOut.getLastBooking().getBookerId());
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start")
                .isEqualTo(fullItemDtoOut.getLastBooking().getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end")
                .isEqualTo(fullItemDtoOut.getLastBooking().getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }
}
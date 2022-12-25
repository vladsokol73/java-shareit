package ru.practicum.shareit.booking.dto;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingBaseTest;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoInTest extends BookingBaseTest {

    @Autowired
    private JacksonTester<BookingDtoIn> jacksonTester;

    @Test
    void toItemDto() throws IOException {
        JsonContent<BookingDtoIn> result = jacksonTester.write(bookingDtoIn);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(bookingDtoIn.getItemId());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingDtoIn.getStartDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingDtoIn.getEndDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }
}
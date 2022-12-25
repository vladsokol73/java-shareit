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
public class BookingDtoOutTest extends BookingBaseTest {

    @Autowired
    private JacksonTester<BookingDtoOut> jacksonTester;

    @Test
    void toItemDto() throws IOException {
        JsonContent<BookingDtoOut> result = jacksonTester.write(bookingDtoOut);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDtoOut.getId());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingDtoOut.getStartDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingDtoOut.getEndDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingDtoOut.getStatus().name());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(bookingDtoOut.getBooker().getId());
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo(bookingDtoOut.getBooker().getName());
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo(bookingDtoOut.getBooker().getEmail());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(bookingDtoOut.getItem().getId());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(bookingDtoOut.getItem().getName());
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo(bookingDtoOut.getItem().getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(bookingDtoOut.getItem().getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId").isEqualTo(bookingDtoOut.getItem().getRequestId());

    }
}

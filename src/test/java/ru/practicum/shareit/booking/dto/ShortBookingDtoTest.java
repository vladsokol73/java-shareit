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
public class ShortBookingDtoTest extends BookingBaseTest {

    @Autowired
    private JacksonTester<ShortBookingDtoOut> jacksonTester;

    @Test
    void toItemDto() throws IOException {
        JsonContent<ShortBookingDtoOut> result = jacksonTester.write(shortBookingDtoOut);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(shortBookingDtoOut.getId());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(shortBookingDtoOut.getBookerId());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(shortBookingDtoOut.getStartDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(shortBookingDtoOut.getEndDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }
}
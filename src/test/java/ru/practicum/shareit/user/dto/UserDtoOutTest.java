package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.UserBaseTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoOutTest extends UserBaseTest {

    @Autowired
    private JacksonTester<UserDtoOut> jacksonTester;

    @Test
    void toUserDto() throws IOException {
        JsonContent<UserDtoOut> result = jacksonTester.write(userDtoOut);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(userDtoOut.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userDtoOut.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(userDtoOut.getEmail());
    }
}
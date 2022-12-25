package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoInTest extends UserDtoOutTest {


    @Autowired
    private JacksonTester<UserDtoIn> jacksonTester;

    @Test
    void toUserDto() throws IOException {
        JsonContent<UserDtoIn> result = jacksonTester.write(userDtoIn);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userDtoIn.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(userDtoIn.getEmail());
    }
}

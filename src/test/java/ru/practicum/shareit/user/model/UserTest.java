package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

    int id = 1;
    String name = "user";
    String email = "user@mail.com";

    @Test
    void testEquals() {
        User x = new User(1, name, email);
        User y = new User(2, name, email);

        assertThat(x.equals(y) && y.equals(x)).isTrue();
        assertThat(x.hashCode() == y.hashCode()).isTrue();
    }

    @Test
    void testNoArgsConstructorAndGettersSetters() {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(email);
    }

    @Test
    void testBuilderAndToString() {
        User user = User.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();

        assertThat(user.toString()).startsWith(User.class.getSimpleName());
    }
}

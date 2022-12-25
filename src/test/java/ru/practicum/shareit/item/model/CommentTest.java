package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemBaseTest;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentTest extends ItemBaseTest {

    @Test
    void testEquals_Symmetric() {
        Comment x = new Comment(1, "comment", item, booker, now);
        Comment y = new Comment(2, "comment", item, booker, now);


        assertThat(x.equals(y) && y.equals(x)).isTrue();
        assertThat(x.hashCode() == y.hashCode()).isTrue();
    }

    @Test
    void noArgsConstructorTest() {
        int id = 1;
        String text = "comment";

        Comment comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(booker);
        comment.onCreate();

        assertThat(comment.getId()).isEqualTo(id);
        assertThat(comment.getText()).isEqualTo(text);
        assertThat(comment.getItem()).isEqualTo(item);
        assertThat(comment.getAuthor()).isEqualTo(booker);
        assertThat(comment.getCreated()).isEqualTo(now);

    }

    @Test
    void toStringTest() {
        int id = 1;
        String text = "comment";

        Comment comment = Comment.builder()
                .id(id)
                .text(text)
                .item(item)
                .author(booker)
                .created(now)
                .build();

        assertThat(comment.toString()).startsWith(comment.getClass().getSimpleName());
    }
}
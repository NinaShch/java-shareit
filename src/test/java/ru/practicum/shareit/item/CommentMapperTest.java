package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

public class CommentMapperTest {

    @Test
    public void toCommentDto() {
        Item item = mock(Item.class);
        User user = mock(User.class);
        when(user.getName()).thenReturn("name");
        Comment comment = new Comment(
               1L,
               "text",
                item,
                user,
                LocalDateTime.now()
        );
        CommentDto dto = CommentMapper.toCommentDto(comment);

        assertEquals("ids not match", comment.getId(), dto.getId());
        assertEquals("texts not match", comment.getText(), dto.getText());
        assertEquals("users not match", "name", dto.getAuthorName());
        assertEquals("createds not match", comment.getCreated(), dto.getCreated());
    }

    @Test
    public void toComment() {
        Item item = mock(Item.class);
        User user = mock(User.class);
        when(user.getName()).thenReturn("name");
        CommentDto dto = new CommentDto(
               1L,
               "text",
                "author",
                LocalDateTime.now()
        );
        Comment comment = CommentMapper.toComment(dto, user, item);

        assertEquals("ids not match", dto.getId(), comment.getId());
        assertEquals("texts not match", dto.getText(), comment.getText());
        assertEquals("users not match", user, comment.getAuthor());
        assertEquals("items not match", item, comment.getItem());
        assertEquals("createds not match", dto.getCreated(), comment.getCreated());
    }
}

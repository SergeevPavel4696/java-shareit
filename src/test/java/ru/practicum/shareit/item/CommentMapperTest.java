package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest {

    private final int userId = 1;
    private final String authorName = "Имя";
    private final String email = "mail@mail.ya";
    private final User author = new User(userId, authorName, email);

    private final int itemId = 2;
    private final String itemName = "Название";
    private final String description = "Описание";
    private final boolean available = true;
    private final int itemRequestId = 3;
    private final Item item = new Item(itemId, itemName, description, available, author, itemRequestId);

    private final int commentId = 4;
    private final String text = "Комментарий";
    private final Comment comment = new Comment(commentId, text, item, author);
    private final CommentDto commentDto = CommentDto.builder().id(commentId).text(text).itemId(itemId).authorName(authorName).build();

    @Test
    public void commentToCommentDtoTest() {
        CommentDto commentDto = CommentMapper.convert(comment);
        assertEquals(commentId, commentDto.getId());
        assertEquals(text, commentDto.getText());
        assertEquals(itemId, commentDto.getItemId());
        assertEquals(authorName, commentDto.getAuthorName());
    }

    @Test
    public void commentDtoToCommentTest() {
        Comment comment = CommentMapper.convert(commentDto, item, author);
        assertEquals(commentId, comment.getId());
        assertEquals(text, comment.getText());
        assertEquals(itemId, comment.getItem().getId());
        assertEquals(authorName, comment.getAuthor().getName());
    }
}

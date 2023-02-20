package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.validator.CommentValidator;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommentValidatorTest {
    private final User author = new User(1, "Автор", "author@mail.ya");
    private final User owner = new User(2, "Владелец", "owner@mail.ya");
    private final Item item = new Item(1, "Название", "Описание", true, owner, null);
    private final Comment commentWithoutText = Comment.builder().id(1).text(null).item(item).author(author).build();
    private final Comment commentWithoutItem = new Comment(1, "Комментарий", null, author);
    private final Comment commentWithoutAuthor = new Comment(1, "Комментарий", item, null);
    private final CommentDto commentWithIncorrectText = new CommentDto(1, "", item.getId(), author.getName());

    @Test
    public void commentValidateTest() {
        assertThrows(ValidationException.class, () -> CommentValidator.validate(commentWithoutText));
        assertThrows(ValidationException.class, () -> CommentValidator.validate(commentWithoutItem));
        assertThrows(ValidationException.class, () -> CommentValidator.validate(commentWithoutAuthor));
    }

    @Test
    public void commentDtoValidateTest() {
        assertThrows(ValidationException.class, () -> CommentValidator.validate(commentWithIncorrectText));
    }
}

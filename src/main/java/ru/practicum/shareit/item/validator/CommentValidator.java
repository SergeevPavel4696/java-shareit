package ru.practicum.shareit.item.validator;

import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;

public class CommentValidator {
    public static void validate(Comment comment) {
        if (comment.getText() == null) {
            throw new ValidationException("Текст комментария обязателен.");
        }
        if (comment.getItem() == null) {
            throw new ValidationException("Комментируемая вещь обязательна.");
        }
        if (comment.getAuthor() == null) {
            throw new ValidationException("Комментатор вещи обязателен.");
        }
    }

    public static void validate(CommentDto commentDto) {
        if (commentDto.getText() != null && commentDto.getText().isEmpty()) {
            throw new ValidationException("Текст комментария указан некорректно.");
        }
    }
}

package ru.practicum.shareit.item.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class CommentMapper {
    public static Comment convert(CommentDto commentDto, Item item, User author) {
        return new Comment(commentDto.getId(), commentDto.getText(), item, author);
    }

    public static CommentDto convert(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getItem().getId(), comment.getAuthor().getName());
    }
}

package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static CommentDto mapToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static Comment mapToComment(NewCommentRequest newComment, User author, Item item) {
        Comment comment = new Comment();
        comment.setText(newComment.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

}

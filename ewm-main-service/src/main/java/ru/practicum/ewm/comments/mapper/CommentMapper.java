package ru.practicum.ewm.comments.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentMapper {
    public Comment toComment(CommentDto commentDto, User user, Event event) {
        return Comment.builder()
                .text(commentDto.getText())
                .event(event)
                .user(user)
                .created(LocalDateTime.now())
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .eventId(comment.getEvent().getId())
                .userId(comment.getUser().getId())
                .created(LocalDateTime.now())
                .build();
    }
}

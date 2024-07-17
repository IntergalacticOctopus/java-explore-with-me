package ru.practicum.ewm.comments.service;

import ru.practicum.ewm.comments.dto.CommentDto;

import java.util.List;

public interface CommentService {
    void deleteByAdmin(int commentId);

    CommentDto create(int userId, int eventId, CommentDto commentDto);

    CommentDto update(int userId, int commentId, CommentDto updateText);

    void deleteById(int userId, int commentId);

    CommentDto getById(int commentId);

    List<CommentDto> getAllByEvent(int eventId, int from, int size);
}

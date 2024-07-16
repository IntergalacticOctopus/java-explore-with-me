package ru.practicum.ewm.comments.service;

import ru.practicum.ewm.comments.dto.CommentDto;

import java.util.List;

public interface CommentService {
    void deleteByAdmin(Integer commentId);

    CommentDto create(Integer userId, Integer eventId, CommentDto commentDto);

    CommentDto update(Integer userId, Integer commentId, CommentDto updateText);

    void deleteById(Integer userId, Integer commentId);

    CommentDto getById(Integer commentId);

    List<CommentDto> getAllByEvent(Integer eventId, Integer from, Integer size);
}

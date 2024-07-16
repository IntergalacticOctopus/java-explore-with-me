package ru.practicum.ewm.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.mapper.CommentMapper;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.comments.repository.CommentRepository;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exception.errors.DataConflictException;
import ru.practicum.ewm.exception.errors.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Transactional
    public void deleteByAdmin(Integer commentId) {
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public CommentDto create(Integer userId, Integer eventId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        Comment comment = commentMapper.toComment(commentDto, user, event);
        comment.setCreated(LocalDateTime.now());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto update(Integer userId, Integer commentId, CommentDto updateText) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Comment comment = getEntityById(commentId);
        CommentDto dto = commentMapper.toCommentDto(comment);
        if (dto.getUserId() != (user.getId())) {
            throw new DataConflictException("Comment must belong to the user");
        }
        comment.setText(updateText.getText());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Transactional
    public void deleteById(Integer userId, Integer commentId) {
        List<Comment> commentList = commentRepository.getCommentByUserId(userId);
        if (commentList.isEmpty()) {
            throw new DataConflictException("Comments not found");
        }
        for (Comment comment : commentList) {
            if (comment.getId() != commentId) {
                throw new DataConflictException("Comment must belong to the user");
            } else {
                commentRepository.deleteById(commentId);
            }
        }
    }

    @Override
    public CommentDto getById(Integer commentId) {
        return commentMapper.toCommentDto(commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found")));
    }

    @Override
    public List<CommentDto> getAllByEvent(Integer eventId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> commentList = commentRepository.getByEventIdOrderByCreatedDesc(eventId, pageable);
        return commentList.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public Comment getEntityById(Integer id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
    }
}
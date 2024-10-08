package ru.practicum.ewm.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
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
    public void deleteByAdmin(int commentId) {
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public CommentDto create(int userId, int eventId, NewCommentDto newCommentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        Comment comment = commentMapper.toComment(newCommentDto, user, event);
        comment.setCreated(LocalDateTime.now());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto update(int userId, int commentId, NewCommentDto newCommentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Comment comment = getEntityById(commentId);
        CommentDto dto = commentMapper.toCommentDto(comment);
        if (dto.getUserId() != (user.getId())) {
            throw new DataConflictException("Comment must belong to the user");
        }
        comment.setText(newCommentDto.getText());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Transactional
    public void deleteById(int userId, int commentId) {
        Comment savedComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        if (savedComment.getUser().getId() == userId) {
            commentRepository.deleteById(commentId);
        } else {
            throw new DataConflictException("The comment does not belong to the user");
        }
    }

    @Override
    public CommentDto getById(int commentId) {
        return commentMapper.toCommentDto(commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found")));
    }

    @Override
    public List<CommentDto> getAllByEvent(int eventId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> commentList = commentRepository.getByEventIdOrderByCreatedDesc(eventId, pageable);
        if (commentList.isEmpty()) {
            throw new NotFoundException(String.format("Comment by eventId = %s not found", eventId));
        }
        return commentList.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private Comment getEntityById(int id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
    }
}
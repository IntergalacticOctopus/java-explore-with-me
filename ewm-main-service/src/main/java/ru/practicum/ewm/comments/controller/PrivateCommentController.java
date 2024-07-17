package ru.practicum.ewm.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable int userId,
                             @PathVariable int eventId,
                             @RequestBody @Valid CommentDto commentDto) {
        log.info("Creating comment with userId = {}, eventId = {}, commentDto = {}", userId, eventId, commentDto);
        return commentService.create(userId, eventId, commentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(@PathVariable int userId,
                             @PathVariable int commentId,
                             @RequestBody @Valid CommentDto commentDto) {
        log.info("Updating comment with userId = {}, commentId = {}, commentDto = {}", userId, commentId, commentDto);
        return commentService.update(userId, commentId, commentDto);
    }

    @DeleteMapping("/{commentId}")
    public void deleteById(@PathVariable int userId,
                           @PathVariable int commentId) {
        log.info("Deleting comment with userId = {}, commentId = {}", userId, commentId);
        commentService.deleteById(userId, commentId);
    }

    @GetMapping("/{commentId}")
    public CommentDto getById(@PathVariable int userId,
                              @PathVariable int commentId) {
        log.info("Getting comment with userId = {}, commentId = {}", userId, commentId);
        return commentService.getById(commentId);
    }

    @GetMapping
    public List<CommentDto> getAllByEvent(@RequestParam int eventId,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all comments by event with eventId= {}", eventId);
        return commentService.getAllByEvent(eventId, from, size);
    }

}
package ru.practicum.ewm.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.comments.service.CommentService;

import javax.validation.Valid;

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
                             @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Creating comment with userId = {}, eventId = {}, newCommentDto = {}", userId, eventId, newCommentDto);
        return commentService.create(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(@PathVariable int userId,
                             @PathVariable int commentId,
                             @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Updating comment with userId = {}, commentId = {}, newCommentDto = {}", userId, commentId, newCommentDto);
        return commentService.update(userId, commentId, newCommentDto);
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

}
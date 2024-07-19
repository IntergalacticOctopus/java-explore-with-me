package ru.practicum.ewm.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comments.service.CommentService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    public void deleteByAdmin(@PathVariable int commentId) {
        log.info("Admin deleted comment with id =  {}", commentId);
        commentService.deleteByAdmin(commentId);
    }
}
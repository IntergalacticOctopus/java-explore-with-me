package ru.practicum.ewm.comments.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.comments.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> getByEventIdOrderByCreatedDesc(Integer id, Pageable pageable);

    List<Comment> getCommentByUserId(Integer userId);
}
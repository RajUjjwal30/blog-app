package org.blog.blog_application.services;

import org.blog.blog_application.dtos.CommentDto;

import java.util.List;

public interface CommentService {
    void createComment(CommentDto commentDto);

    List<CommentDto> getCommentsByPostId(Long postId);

    void deleteComment(Long commentId, String currentUsername, boolean isAdmin);

    void updateComment(Long commentId, String newContent, String currentUsername, boolean isAdmin);
}

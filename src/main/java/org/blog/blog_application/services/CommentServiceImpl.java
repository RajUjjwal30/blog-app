package org.blog.blog_application.services;

import org.blog.blog_application.dtos.CommentDto;
import org.blog.blog_application.mapper.CommentMapper;
import org.blog.blog_application.models.Comment;
import org.blog.blog_application.models.Post;
import org.blog.blog_application.repositories.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{
    private CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public void createComment(CommentDto commentDto) {
        Comment comment = CommentMapper.toEntity(commentDto);
        commentRepository.save(comment);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        List<CommentDto> dtoList = new ArrayList<>();

        for(Comment comment : comments){
            dtoList.add(CommentMapper.toDto(comment));
        }
        return dtoList;
    }

    @Override
    public void deleteComment(Long commentId, String currentUsername, boolean isAdmin) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        String postAuthor = comment.getPost().getAuthor() != null
                ? comment.getPost().getAuthor().getUsername() : null;
        if (!isAdmin && !currentUsername.equals(postAuthor)) {
            throw new RuntimeException("You can only delete comments on your own posts");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public void updateComment(Long commentId, String newContent, String currentUsername, boolean isAdmin) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        String postAuthor = comment.getPost().getAuthor() != null
                ? comment.getPost().getAuthor().getUsername() : null;
        if (!isAdmin && !currentUsername.equals(postAuthor)) {
            throw new RuntimeException("You can only edit comments on your own posts");
        }
        comment.setContent(newContent);
        commentRepository.save(comment);
    }
}
